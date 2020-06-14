package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.block.BloodSqueezerBlock;
import com.github.Crupette.biomechanics.screen.BloodSqueezerScreenHandler;
import com.github.Crupette.biomechanics.tag.BiomechanicsTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.Iterator;
import java.util.Map;

public class BloodSqueezerBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable {
    private static final int[] TOP_SLOTS = new int[] {0, 2};
    private static final int[] BOTTOM_SLOTS = new int[] {1, 3};
    private static final int[] SIDE_SLOTS = new int[] {1, 2};
    private DefaultedList<ItemStack> inventory;

    private int bloodBottles;

    private int burnTime;
    private int fuelTime;
    private int squeezeTime;
    private int squeezeTimeTotal;

    public final PropertyDelegate propertyDelegate;

    public static final Map<Item, Integer> FUEL_MAP = AbstractFurnaceBlockEntity.createFuelTimeMap();

    public BloodSqueezerBlockEntity() {
        super(BiomechanicsBlockEntities.BLOOD_SQUEEZER);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);

        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index){
                    case 0: return BloodSqueezerBlockEntity.this.burnTime;
                    case 1: return BloodSqueezerBlockEntity.this.fuelTime;
                    case 2: return BloodSqueezerBlockEntity.this.squeezeTime;
                    case 3: return BloodSqueezerBlockEntity.this.squeezeTimeTotal;
                    case 4: return BloodSqueezerBlockEntity.this.bloodBottles;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch (index){
                    case 0: BloodSqueezerBlockEntity.this.burnTime = value;
                    case 1: BloodSqueezerBlockEntity.this.fuelTime = value;
                    case 2: BloodSqueezerBlockEntity.this.squeezeTime = value;
                    case 3: BloodSqueezerBlockEntity.this.squeezeTimeTotal = value;
                    case 4: BloodSqueezerBlockEntity.this.bloodBottles = value;
                }
            }

            public int size() {
                return 5;
            }
        };
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        switch (side){
            case UP: return TOP_SLOTS;
            case DOWN: return BOTTOM_SLOTS;
            default: return SIDE_SLOTS;
        }
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if(dir == Direction.DOWN && slot == 1){
            Item item = stack.getItem();
            if(item != Items.WATER_BUCKET && item != Items.BUCKET) return false;
        }
        return true;
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        Iterator it = this.inventory.iterator();
        ItemStack stack;

        do{
            if(!it.hasNext()) return true;
            stack = (ItemStack) it.next();
        }while (stack.isEmpty());
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areTagsEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        if (slot == 0 && !bl) {
            this.squeezeTimeTotal = 200;
            this.squeezeTime = 0;
            this.markDirty();
        }

    }

    public boolean isValid(int slot, ItemStack stack) {
        if(slot == 3){
            return false;
        }else if (slot == 2) {
            return stack.getItem() == Items.GLASS_BOTTLE;
        } else if (slot == 1) {
            ItemStack itemStack = this.inventory.get(1);
            return FUEL_MAP.containsKey(stack.getItem()) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
        }
        return true;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void tick() {
        ItemStack meatStack = this.inventory.get(0);
        boolean dirty = false;
        boolean burning = this.burnTime > 0;

        if(this.burnTime > 0){
            this.burnTime--;
        }

        if(!this.world.isClient){
            /*Biomechanics.log(Level.INFO,
                    "burnTime: " + this.burnTime +
                    " fuelTime: " + this.fuelTime +
                    " squeezeTime: " + this.squeezeTime +
                    " squeezeTimeTotal: " + this.squeezeTimeTotal);*/
            ItemStack fuelStack = this.inventory.get(1);
            if(this.burnTime <= 0 && (fuelStack.isEmpty() || meatStack.isEmpty())){
                this.squeezeTime = 0;
            }else{
                boolean isMeat = BiomechanicsTags.MEATS.values().contains(meatStack.getItem());
                if(this.burnTime <= 0 && isMeat){
                    this.burnTime = FUEL_MAP.get(fuelStack.getItem());
                    this.fuelTime = this.burnTime;
                    if(this.burnTime > 0){
                        dirty = true;
                        if(!fuelStack.isEmpty()){
                            Item fuelItem = fuelStack.getItem();
                            fuelStack.decrement(1);
                            if(fuelStack.isEmpty()){
                                Item fuelRemainder = fuelItem.getRecipeRemainder();
                                this.inventory.set(1, fuelRemainder == null ? ItemStack.EMPTY : new ItemStack(fuelRemainder));
                            }
                        }
                    }
                }

                if(this.burnTime > 0 && this.bloodBottles < 3){
                    if(this.squeezeTime >= this.squeezeTimeTotal){
                        this.squeezeTime = 0;
                        this.squeezeTimeTotal = 200;
                        this.bloodBottles++;
                        meatStack.decrement(1);
                        dirty = true;
                    }else {
                        ++this.squeezeTime;
                    }
                }else{
                    this.squeezeTime = 0;
                }
            }
            if(burning != (this.burnTime > 0)){
                dirty = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BloodSqueezerBlock.LIT, burning), 3);
            }

            ItemStack bottleStack = this.inventory.get(2);
            ItemStack outputStack = this.inventory.get(3);
            if(this.bloodBottles > 0 && !bottleStack.isEmpty()){
                ItemStack bloodBottle = new ItemStack(Items.ROTTEN_FLESH);
                if(outputStack.isEmpty() || (outputStack.getItem().equals(bloodBottle.getItem()) && outputStack.getCount() < outputStack.getMaxCount())){
                    dirty = true;
                    if(outputStack.isEmpty()) {
                        this.inventory.set(3, outputStack.copy());
                    }else{
                        outputStack.increment(1);
                    }
                    bottleStack.decrement(1);
                    this.bloodBottles--;
                }
            }
        }

        if(dirty){
            this.markDirty();
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);

        this.burnTime = tag.getShort("burnTime");
        this.fuelTime = tag.getShort("fuelTime");

        this.squeezeTime = tag.getShort("squeezeTime");
        this.squeezeTimeTotal = tag.getShort("squeezeTimeTotal");

        this.bloodBottles = tag.getShort("bloodBottles");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);

        Inventories.toTag(tag, this.inventory);

        tag.putShort("burnTime", (short) this.burnTime);
        tag.putShort("fuelTime", (short) this.fuelTime);

        tag.putShort("squeezeTime", (short) this.squeezeTime);
        tag.putShort("squeezeTimeTotal", (short) this.squeezeTimeTotal);

        tag.putShort("bloodBottles", (short) this.bloodBottles);

        return tag;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.biomechanics.blood_squeezer");
    }

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BloodSqueezerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
