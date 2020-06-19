package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.block.BoilerBlock;
import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import com.github.Crupette.biomechanics.screen.BoilerScreenHandler;
import com.github.Crupette.biomechanics.screen.DigestorScreenHandler;
import com.github.Crupette.biomechanics.util.network.CirculatoryNetwork;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Iterator;
import java.util.Optional;

public class BoilerBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable, Biological {
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] SIDE_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[] {1};
    protected DefaultedList<ItemStack> inventory;

    protected final PropertyDelegate propertyDelegate;
    private BlockPos parent;

    private int calorieCost;
    private float cookTime;
    private int cookTimeTotal;
    private boolean ignoreChecks;
    private SmeltingRecipe currentRecipe;

    public static int CALORIE_MIN = 9;

    private CirculatoryNetwork network;

    public BoilerBlockEntity() {
        super(BiomechanicsBlockEntities.BOILER);

        this.calorieCost = 24;
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index){
                    case 0: return (int)BoilerBlockEntity.this.cookTime;
                    case 1: return BoilerBlockEntity.this.cookTimeTotal;
                    case 2: return BoilerBlockEntity.this.calorieCost;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch (index){
                    case 0: BoilerBlockEntity.this.cookTime = value;
                    break;
                    case 1: BoilerBlockEntity.this.cookTimeTotal = value;
                    break;
                    case 2: BoilerBlockEntity.this.calorieCost = value;
                    break;
                }
            }

            @Override
            public int size() {
                return 3;
            }
        };
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.biomechanics.boiler");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BoilerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        switch (side){
            case DOWN: return BOTTOM_SLOTS;
            case UP: return TOP_SLOTS;
            default: return SIDE_SLOTS;
        }
    }

    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 1;
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
        if(slot == 0){
            this.currentRecipe = null;
            this.ignoreChecks = false;
        }
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
        if(slot == 1) return false;
        return this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(stack), this.world).isPresent();
    }

    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void tick() {
        if(!world.isClient && this.network != null){
            ItemStack boilingStack = this.inventory.get(0);
            if(boilingStack.isEmpty()){
                this.currentRecipe = null;
            }else{
                if(this.currentRecipe == null && !this.ignoreChecks){
                    Optional<SmeltingRecipe> smeltingRecipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, this.world);
                    smeltingRecipe.ifPresent(recipe -> this.currentRecipe = recipe);
                    if(this.currentRecipe != null){
                        this.cookTime = 0;
                        this.cookTimeTotal = this.currentRecipe.getCookTime();
                    }
                }else if(this.currentRecipe != null){
                    int oxygenRequested = (int)((float)this.calorieCost * 0.75f);

                    int calories = this.network.requestCalories(this.calorieCost);
                    int oxygen = this.network.requestOxygen((int)((float)this.calorieCost * 0.75f));

                    double efficiency = ((2 * Math.sqrt((calories - 4.f) / 20.f)) * ((float)oxygen / (float)oxygenRequested)) - 1;
                    this.cookTime += efficiency;
                    if(this.cookTime < 0) this.cookTime = 0;

                    if(this.cookTime >= this.cookTimeTotal){
                        this.cookTime = 0;
                        this.craftRecipe(this.currentRecipe);
                        this.currentRecipe = null;
                        this.markDirty();
                    }
                }
            }
        }
    }

    protected boolean canAcceptRecipeOutput(Recipe<?> recipe) {
        if (!this.inventory.get(0).isEmpty() && recipe != null) {
            ItemStack recipeOutputStack = recipe.getOutput();
            if (recipeOutputStack.isEmpty()) {
                return false;
            } else {
                ItemStack outputStack = this.inventory.get(1);
                if (outputStack.isEmpty()) {
                    return true;
                } else if (!outputStack.isItemEqualIgnoreDamage(recipeOutputStack)) {
                    return false;
                } else if (outputStack.getCount() < this.getMaxCountPerStack() && outputStack.getCount() < outputStack.getMaxCount()) {
                    return true;
                } else {
                    return outputStack.getCount() < recipeOutputStack.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    private void craftRecipe(Recipe<?> recipe) {
        if (recipe != null && this.canAcceptRecipeOutput(recipe)) {
            ItemStack inputStack = this.inventory.get(0);
            ItemStack recipeOutputStack = recipe.getOutput();
            ItemStack outputStack = this.inventory.get(1);
            if (outputStack.isEmpty()) {
                this.inventory.set(1, recipeOutputStack.copy());
            } else if (outputStack.getItem() == recipeOutputStack.getItem()) {
                outputStack.increment(1);
            }

            inputStack.decrement(1);
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);

        this.calorieCost = tag.getInt("calorieCost");
        this.cookTime = tag.getFloat("cookTime");
        this.cookTimeTotal = tag.getInt("cookTimeTotal");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        Inventories.toTag(tag, this.inventory);

        tag.putInt("calorieCost", this.calorieCost);
        tag.putFloat("cookTime", this.cookTime);
        tag.putInt("cookTimeTotal", this.cookTimeTotal);

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

    }

}
