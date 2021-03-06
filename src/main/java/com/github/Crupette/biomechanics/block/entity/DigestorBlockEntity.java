package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.DigestorBlock;
import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import com.github.Crupette.biomechanics.screen.DigestorScreenHandler;
import com.github.Crupette.biomechanics.util.network.CirculatoryNetwork;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
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

import java.util.Iterator;

public class DigestorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, BlockEntityClientSerializable, SidedInventory, Tickable, BiologicalNetworked{
    private static final int[] TOP_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 3};
    protected DefaultedList<ItemStack> inventory;

    protected final PropertyDelegate propertyDelegate;
    private BlockPos parent;

    private int storedCalories;
    private int storedMaximum;

    private int processingCalories;
    private int processingMaximum;

    private CirculatoryNetwork network;

    public DigestorBlockEntity() {
        super(BiomechanicsBlockEntities.DIGESTOR);

        this.inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index){
                    case 0: return DigestorBlockEntity.this.storedCalories;
                    case 1: return DigestorBlockEntity.this.storedMaximum;
                    case 2: return DigestorBlockEntity.this.processingCalories;
                    case 3: return DigestorBlockEntity.this.processingMaximum;
                    case 4: return DigestorBlockEntity.this.network != null ? DigestorBlockEntity.this.network.getCalorieStorage() : 0;
                    case 5: return DigestorBlockEntity.this.network != null ? DigestorBlockEntity.this.network.getCalorieStorageCapacity() : 0;
                    case 6: return DigestorBlockEntity.this.network != null ? DigestorBlockEntity.this.network.getCalorieOverflow() : 0;
                    case 7: return DigestorBlockEntity.this.network != null ? DigestorBlockEntity.this.network.getBloodCalories() : 0;
                    case 8: return DigestorBlockEntity.this.network != null ? DigestorBlockEntity.this.network.getHeartHealth() : 0;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch (index){
                    case 0: DigestorBlockEntity.this.storedCalories = value;
                        break;
                    case 1: DigestorBlockEntity.this.storedMaximum = value;
                        break;
                    case 2: DigestorBlockEntity.this.processingCalories = value;
                        break;
                    case 3: DigestorBlockEntity.this.processingMaximum = value;
                        break;
                    case 4:   if(DigestorBlockEntity.this.network != null) DigestorBlockEntity.this.network.setCalorieStorage(value);
                        break;
                    case 5:   if(DigestorBlockEntity.this.network != null) DigestorBlockEntity.this.network.setCalorieStorageCapacity(value);
                        break;
                    case 6:   if(DigestorBlockEntity.this.network != null) DigestorBlockEntity.this.network.setCalorieOverflow(value);
                        break;
                    case 7:   if(DigestorBlockEntity.this.network != null) DigestorBlockEntity.this.network.setBloodCalories(value);
                        break;
                    case 8:   if(DigestorBlockEntity.this.network != null) DigestorBlockEntity.this.network.setHeartHealth(value);

                }
            }

            @Override
            public int size() {
                return 9;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Biomechanics.getTranslated("container", "digestor");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DigestorScreenHandler(syncId, inv, this, this.propertyDelegate);
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

        if (slot == 0 && !bl) {
            this.markDirty();
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
        if(slot == 0 && stack.getItem().isFood()) return true;
        if(slot == 1 && stack.getItem() == BiomechanicsItems.STOMACH) return true;
        if(slot == 2 && stack.getItem() == BiomechanicsItems.SMALL_INTESTINE) return true;
        if(slot == 3 && stack.getItem() == BiomechanicsItems.DECAY_STABILIZER) return true;
        return false;
    }

    public void clear() {
        this.inventory.clear();
    }

    private void damageOrgan(ItemStack organStack){
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
                    if(this.inventory.get(3).isEmpty()){
                        this.world.playSound(null, this.pos, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.BLOCKS, 1.f, (float) ((Math.random() * 0.4f) + 0.8f));
                        health -= 2;
                        invincibleTicks = 20;
                    }
                }
            }
            if(health < 0) {
                organStack.decrement(1);
                this.markDirty();
            }else {
                CompoundTag tag = organStack.getTag();
                tag.putInt("suffocationTicks", suffocationTicks);
                tag.putInt("invincibleTicks", invincibleTicks);
                tag.putInt("health", health);

                organStack.setTag(tag);
            }
        }
    }

    private void healOrgan(ItemStack organStack){
        if(!organStack.isEmpty()) {
            int suffocationTicks = organStack.getOrCreateTag().getInt("suffocationTicks");
            int invincibleTicks = organStack.getOrCreateTag().getInt("invincibleTicks");
            int health = organStack.getOrCreateTag().getInt("health");

            suffocationTicks++;
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

    private void damageOrgans(){
        ItemStack stomach = this.inventory.get(1);
        ItemStack smallIntestine = this.inventory.get(2);

        this.damageOrgan(stomach);
        this.damageOrgan(smallIntestine);
    }

    private void healOrgans(){
        ItemStack stomach = this.inventory.get(1);
        ItemStack smallIntestine = this.inventory.get(2);

        this.healOrgan(stomach);
        this.healOrgan(smallIntestine);
    }

    @Override
    public void tick() {
        boolean dirty = false;
        if(!world.isClient){
            if(this.network == null){
                damageOrgans();
                return;
            }

            ItemStack foodStack = this.inventory.get(0);

            ItemStack stomachStack = this.inventory.get(1);
            ItemStack smallIntestineStack = this.inventory.get(2);

            if(!stomachStack.isEmpty() && !smallIntestineStack.isEmpty()) {
                int sustainOxygen = this.network.requestOxygen(2);
                int sustainCalories = this.network.requestCalories(2);

                int capacity = (stomachStack.getOrCreateTag().getInt("health") * 800);
                int absorbtionRate = (smallIntestineStack.getOrCreateTag().getInt("health") * 3);

                if (sustainCalories < 2 || sustainOxygen < 2) {
                    damageOrgans();
                }else{
                    healOrgans();
                }

                if(this.processingCalories - absorbtionRate < 0){
                    this.storedCalories += processingCalories;
                    this.processingCalories = 0;
                }else{
                    this.processingCalories -= absorbtionRate;
                    this.storedCalories += absorbtionRate;
                }

                this.storedMaximum = capacity;
                if(this.storedCalories > this.storedMaximum) this.storedCalories = this.storedMaximum;

                if(this.processingCalories == 0){
                    if(this.processingMaximum != 0){
                        dirty = true;
                    }
                    if(!foodStack.isEmpty()){
                        this.processingCalories +=
                                foodStack.getItem().getFoodComponent().getHunger() *
                                foodStack.getItem().getFoodComponent().getSaturationModifier() *
                                4000;
                        this.processingMaximum = this.processingCalories;
                        foodStack.decrement(1);
                    }else{
                        this.processingMaximum = 0;
                    }
                }
            }
            if(dirty){
                this.markDirty();
            }
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);

        this.storedCalories = tag.getInt("storedCalories");
        this.storedMaximum = tag.getInt("storedMaximum");
        this.processingCalories = tag.getInt("processingCalories");
        this.processingMaximum = tag.getInt("processingMaximum");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        Inventories.toTag(tag, this.inventory);

        tag.putInt("storedCalories", this.storedCalories);
        tag.putInt("storedMaximum", this.storedMaximum);
        tag.putInt("processingCalories", this.processingCalories);
        tag.putInt("processingMaximum", this.processingMaximum);

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
        return 8192;
    }

    @Override
    public void onBeat() {
        this.storedCalories -= this.network.provideCalories(this.storedCalories);
        if(!this.world.isClient){
            this.sync();
        }
    }

    //Workaround to fix client-server GUI sync issues (if this can be done better please do a pull request / let me know)
    @Override
    public void fromClientTag(CompoundTag tag) {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);

        this.storedCalories = tag.getInt("storedCalories");
        this.storedMaximum = tag.getInt("storedMaximum");
        this.processingCalories = tag.getInt("processingCalories");
        this.processingMaximum = tag.getInt("processingMaximum");

        if(this.network == null) this.network = new CirculatoryNetwork(null);
        this.network.fromTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        Inventories.toTag(tag, this.inventory);

        tag.putInt("storedCalories", this.storedCalories);
        tag.putInt("storedMaximum", this.storedMaximum);
        tag.putInt("processingCalories", this.processingCalories);
        tag.putInt("processingMaximum", this.processingMaximum);

        if(this.network != null) this.network.toTag(tag);
        return tag;
    }
}
