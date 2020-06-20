package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.HeartCaseBlock;
import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import com.github.Crupette.biomechanics.screen.HeartCaseScreenHandler;
import com.github.Crupette.biomechanics.screen.OxygenPumpScreenHandler;
import com.github.Crupette.biomechanics.util.network.CirculatoryNetwork;
import com.github.Crupette.biomechanics.util.tree.GenericTree;
import com.github.Crupette.biomechanics.util.tree.GenericTreeNode;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OxygenPumpBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, SidedInventory, Tickable, BiologicalNetworked {
    private static final int[] TOP_SLOTS = new int[]{0, 1};
    private static final int[] SIDE_SLOTS = new int[]{0, 1};
    protected DefaultedList<ItemStack> inventory;

    protected final PropertyDelegate propertyDelegate;
    private BlockPos parent;
    private boolean needsBreath;

    private int breathDelay;
    private int storedOxygen;
    private int storedOxygenMax = 1;

    private CirculatoryNetwork network;

    public OxygenPumpBlockEntity() {
        super(BiomechanicsBlockEntities.OXYGEN_PUMP);

        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index){
                    case 0: return OxygenPumpBlockEntity.this.storedOxygen;
                    case 1: return OxygenPumpBlockEntity.this.storedOxygenMax;
                    case 2: return OxygenPumpBlockEntity.this.breathDelay;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch (index){
                    case 0: OxygenPumpBlockEntity.this.storedOxygen = value;
                    break;
                    case 1: OxygenPumpBlockEntity.this.storedOxygenMax = value;
                    break;
                    case 2: OxygenPumpBlockEntity.this.breathDelay = value;
                }
            }

            @Override
            public int size() {
                return 3;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Biomechanics.getTranslated("container", "oxygen_pump");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new OxygenPumpScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        switch (side){
            case DOWN: return new int[] {};
            case UP: return TOP_SLOTS;
            default: return SIDE_SLOTS;
        }
    }

    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    public int size() {
        return this.inventory.size();
    }

    public boolean isEmpty() {
        Iterator var1 = this.inventory.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack)var1.next();
        } while(itemStack.isEmpty());

        return false;
    }

    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areTagsEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    public boolean isValid(int slot, ItemStack stack) {
        if(slot == 1 && stack.getItem() == BiomechanicsItems.DECAY_STABILIZER) return true;
        if(slot == 0 && stack.getItem() == BiomechanicsItems.LUNGS) return true;
        return false;
    }

    public void clear() {
        this.inventory.clear();
    }

    private void damageOrgan(){
        ItemStack organStack = this.inventory.get(0);
        if(!organStack.isEmpty()) {
            int suffocationTicks = organStack.getOrCreateTag().getInt("suffocationTicks");
            int invincibleTicks = organStack.getOrCreateTag().getInt("invincibleTicks");
            int health = organStack.getOrCreateTag().getInt("health");

            if(suffocationTicks > 0) {
                suffocationTicks--;
            }else{
                if(invincibleTicks > 0){
                    invincibleTicks--;
                }else{
                    if(!this.inventory.get(1).isEmpty()){
                        ItemStack decayStabilizer = this.inventory.get(1);
                        decayStabilizer.setDamage(decayStabilizer.getDamage() - 1);
                        this.inventory.set(1, decayStabilizer);
                    }else{
                        this.world.playSound(null, this.pos, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.BLOCKS, 1.f, (float) ((Math.random() * 0.4f) + 0.8f));
                        health -= 2;
                        invincibleTicks = 20;
                    }
                }
            }
            if(health < 0) {
                inventory.set(0, ItemStack.EMPTY);
            }else {
                CompoundTag tag = organStack.getTag();
                tag.putInt("suffocationTicks", suffocationTicks);
                tag.putInt("invincibleTicks", invincibleTicks);
                tag.putInt("health", health);

                organStack.setTag(tag);
            }
        }
    }

    private void healOrgan(){
        ItemStack organStack = this.inventory.get(0);
        if(!organStack.isEmpty()) {
            int suffocationTicks = organStack.getOrCreateTag().getInt("suffocationTicks");
            int invincibleTicks = organStack.getOrCreateTag().getInt("invincibleTicks");
            int health = organStack.getOrCreateTag().getInt("health");

            if((int)(Math.random() * 10) == 1)suffocationTicks++;
            if(suffocationTicks > 20){
                suffocationTicks = 20;
            }

            CompoundTag tag = organStack.getTag();
            tag.putInt("suffocationTicks", suffocationTicks);
            tag.putInt("invincibleTicks", invincibleTicks);
            tag.putInt("health", health);

            organStack.setTag(tag);
        }
    }

    @Override
    public void tick() {
        if(!world.isClient){
            if(this.network == null){
                damageOrgan();
                return;
            }
            if(this.breathDelay > 0) {
                this.breathDelay--;
            }

            ItemStack organStack = this.inventory.get(0);
            if(!organStack.isEmpty()) {
                int sustainOxygen = this.network.requestOxygen(1);
                int sustainCalories = this.network.requestCalories(1);
                int health = organStack.getOrCreateTag().getInt("health");

                if (sustainCalories < 1 || sustainOxygen < 1) {
                    damageOrgan();
                }else{
                    healOrgan();
                }

                if(this.storedOxygen < 100 && this.breathDelay == 0){
                    this.needsBreath = true;
                }

                if(this.needsBreath){
                    sustainOxygen = this.network.requestOxygen(2);
                    sustainCalories = this.network.requestCalories(2);

                    if(health <= 1) health = 2;
                    float efficiency = (((float)(sustainCalories) + (float)(sustainOxygen) + 1) / 5.f) * (1.f - (breathDelay / (health)));

                    this.needsBreath = false;
                    this.storedOxygen = (int) ((health * 80) * efficiency);
                    this.storedOxygenMax = (health * 80);
                    this.breathDelay = health;
                    this.world.playSound(null, this.pos, SoundEvents.ENTITY_PLAYER_BREATH, SoundCategory.BLOCKS, 0.1f, 1.f);
                }
            }
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);

        this.storedOxygen = tag.getInt("oxygen");
        this.breathDelay = tag.getInt("breathDelay");
        this.needsBreath = tag.getBoolean("breathNeeded");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        Inventories.toTag(tag, this.inventory);

        tag.putInt("oxygen", this.storedOxygen);
        tag.putInt("breathDelay", this.breathDelay);
        tag.putBoolean("breathNeeded", this.needsBreath);

        return tag;
    }

    @Override
    public BlockPos getParent() {
        return this.parent;
    }

    @Override
    public void setParent(BlockPos pos) {
        this.parent = pos;
        if (pos == null) {
            this.network = null;
        }else {
            this.network = ((HeartCaseBlockEntity) this.world.getBlockEntity(pos)).network;
        }
    }

    @Override
    public int getCalorieStorageCapacity() {
        return 1024;
    }

    @Override
    public void onBeat() {
        this.storedOxygen -= this.network.provideOxygen(this.storedOxygen);
    }

    public void requestBreath() {
        this.needsBreath = true;
    }
}
