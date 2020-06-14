package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.block.SqueezerBlock;
import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import com.github.Crupette.biomechanics.screen.SqueezerScreenHandler;
import com.github.Crupette.biomechanics.tag.BiomechanicsTags;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.recipe.*;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;

public class SqueezerBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable {
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};
    protected DefaultedList<ItemStack> inventory;

    private int burnTime;
    private int fuelTime;
    private int squeezeTime;
    private int squeezeTimeTotal;
    private int bloodBottles;

    protected final PropertyDelegate propertyDelegate;

    public SqueezerBlockEntity() {
        super(BiomechanicsBlockEntities.SQUEEZER);
        this.inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch(index) {
                    case 0: return SqueezerBlockEntity.this.burnTime;
                    case 1: return SqueezerBlockEntity.this.fuelTime;
                    case 2: return SqueezerBlockEntity.this.squeezeTime;
                    case 3: return SqueezerBlockEntity.this.squeezeTimeTotal;
                    case 4: return SqueezerBlockEntity.this.bloodBottles;
                    default:
                        return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: SqueezerBlockEntity.this.burnTime = value;
                        break;
                    case 1: SqueezerBlockEntity.this.fuelTime = value;
                        break;
                    case 2: SqueezerBlockEntity.this.squeezeTime = value;
                        break;
                    case 3: SqueezerBlockEntity.this.squeezeTimeTotal = value;
                        break;
                    case 4: SqueezerBlockEntity.this.bloodBottles = value;
                        break;
                }

            }

            public int size() {
                return 5;
            }
        };
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);

        this.burnTime = tag.getShort("burnTime");
        this.fuelTime = this.getFuelTime(this.inventory.get(1));

        this.squeezeTime = tag.getShort("squeezeTime");
        this.squeezeTimeTotal = tag.getShort("squeezeTimeTotal");

        this.bloodBottles = tag.getShort("bloodBottles");
    }

    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putShort("burnTime", (short)this.burnTime);

        tag.putShort("squeezeTime", (short)this.squeezeTime);
        tag.putShort("squeezeTimeTotal", (short)this.squeezeTimeTotal);

        tag.putShort("bloodBottles", (short)this.bloodBottles);

        Inventories.toTag(tag, this.inventory);
        return tag;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.biomechanics.squeezer");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new SqueezerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick() {
        ItemStack meatStack = this.inventory.get(0);
        boolean dirty = false;
        boolean burning = this.isBurning();

        if(this.isBurning()){
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
                    this.burnTime = AbstractFurnaceBlockEntity.createFuelTimeMap().get(fuelStack.getItem());
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

                if(this.isBurning() && this.bloodBottles < 3 && isMeat){
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
            if(burning != this.isBurning()){
                dirty = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(SqueezerBlock.LIT, this.isBurning()), 3);
            }

            ItemStack bottleStack = this.inventory.get(2);
            ItemStack outputStack = this.inventory.get(3);
            if(this.bloodBottles > 0 && !bottleStack.isEmpty()){
                ItemStack bloodBottle = new ItemStack(BiomechanicsItems.BLOOD_BOTTLE);
                if(outputStack.isEmpty() || (outputStack.getItem().equals(bloodBottle.getItem()) && outputStack.getCount() < outputStack.getMaxCount())){
                    dirty = true;
                    if(outputStack.isEmpty()) {
                        this.inventory.set(3, bloodBottle.copy());
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

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
        }
    }

    protected int getSqueezeTime() {
        return 200;
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(stack.getItem());
    }

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        } else {
            return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
        }
    }

    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == 1) {
            Item item = stack.getItem();
            if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
                return false;
            }
        }

        return true;
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
        return (ItemStack)this.inventory.get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = (ItemStack)this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areTagsEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        if (slot == 0 && !bl) {
            this.squeezeTimeTotal = this.getSqueezeTime();
            this.squeezeTime = 0;
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
        if (slot == 2) {
            return false;
        } else if (slot != 1) {
            return true;
        } else {
            ItemStack itemStack = (ItemStack)this.inventory.get(1);
            return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
        }
    }

    public void clear() {
        this.inventory.clear();
    }
}
