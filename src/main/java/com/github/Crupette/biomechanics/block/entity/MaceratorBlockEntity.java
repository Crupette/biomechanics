package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.block.MaceratorBlock;
import com.github.Crupette.biomechanics.recipe.BiomechanicsRecipes;
import com.github.Crupette.biomechanics.recipe.MaceratorRecipe;
import com.github.Crupette.biomechanics.util.network.CirculatoryNetwork;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaceratorBlockEntity extends BlockEntity implements SidedInventory, Tickable, BiologicalNetworked {

    private CirculatoryNetwork network = null;
    private BlockPos parent = null;

    private ItemStack processingStack = ItemStack.EMPTY;
    private int processingTimer = 0;

    private List<ItemStack> spitStacks = new ArrayList<>();
    private int spitTimer = 0;

    private MaceratorRecipe currentRecipe = null;

    public MaceratorBlockEntity() {
        super(BiomechanicsBlockEntities.MACERATOR);
    }

    @Override
    public BlockPos getParent() {
        return this.parent;
    }

    @Override
    public void setParent(BlockPos pos) {
        this.parent = pos;
        if(this.parent != null){
            BlockEntity heartCase = this.world.getBlockEntity(pos);
            if(heartCase instanceof HeartCaseBlockEntity) {
                this.network = ((HeartCaseBlockEntity)heartCase).network;
            }
        }
    }

    @Override
    public int getCalorieStorageCapacity() {
        return 1024;
    }

    @Override
    public void onBeat() {
        this.network.requestCalories(4);
        this.network.requestOxygen(6);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.processingStack.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.processingStack;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        this.processingStack.decrement(amount);
        return this.processingStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        this.processingStack = ItemStack.EMPTY;
        this.processingTimer = 0;
        this.currentRecipe = null;
        return this.processingStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.processingTimer = 0;
        this.currentRecipe = null;
        this.processingStack = stack;
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
        this.processingStack = ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        if(!this.processingStack.isEmpty() && this.network != null){
            if(this.currentRecipe == null){
                List<MaceratorRecipe> possibles = this.world.getRecipeManager().getAllMatches(BiomechanicsRecipes.MACERATOR, this, this.world);
                if(possibles.isEmpty()){
                    if(!this.world.isClient) {
                        this.spitStacks.add(this.processingStack.copy());
                    }
                    this.markDirty();
                    this.processingStack = ItemStack.EMPTY;
                    this.spitTimer = 20;
                    return;
                }else{
                    this.currentRecipe = possibles.get(0);
                    this.processingTimer = this.currentRecipe.getTime();
                }
            }

            if(this.processingTimer > 0) {
                int calories = 16;
                int oxygen = 12;
                if(!world.isClient) {
                    calories = this.network.requestCalories(16);
                    oxygen = this.network.requestOxygen(12);
                }
                if(calories >= 16 && oxygen >= 12) {
                    this.processingTimer--;
                    if(this.processingTimer % 5 == 0){
                        this.world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, 0.4f, 1.f);
                    }
                }

                if(this.processingTimer == 0){
                    this.world.playSound(null, this.pos, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.BLOCKS, 0.6f, 1.f);

                    ItemStack swallow = this.currentRecipe.craft(this);
                    this.processingStack.decrement(1);
                    this.currentRecipe = null;
                    this.markDirty();

                    //Try to place in inventory below
                    BlockEntity entityBelow = this.world.getBlockEntity(this.pos.down());
                    if(entityBelow instanceof Inventory){
                        swallow = HopperBlockEntity.transfer(this, (Inventory)entityBelow, swallow, Direction.UP);
                    }

                    if(!swallow.isEmpty()){
                        this.spitStacks.add(swallow.copy());
                        this.spitTimer = 20;
                    }
                }
            }
        }
        if(this.spitTimer > 0){
            this.spitTimer--;

            if(this.spitTimer == 0){
                //TODO: Spit
                BlockState blockState = this.world.getBlockState(this.pos);
                Direction dir = blockState.get(HorizontalFacingBlock.FACING);

                float x = this.pos.getX() + dir.getOffsetX() + 0.5f;
                float y = this.pos.getY() + 1;
                float z = this.pos.getZ() + dir.getOffsetZ() + 0.5f;

                float velx = dir.getOffsetX() / 5.f;
                float vely = 0.2f;
                float velz = dir.getOffsetZ() / 5.f;
                if(!world.isClient) {
                    for (ItemStack item : this.spitStacks) {
                        ItemEntity itemEntity = new ItemEntity(this.world, x, y, z, item);
                        itemEntity.setVelocity(velx, vely, velz);
                        this.world.spawnEntity(itemEntity);
                    }
                    this.spitStacks.clear();
                    this.world.playSound(null, this.pos, SoundEvents.ENTITY_LLAMA_SPIT, SoundCategory.BLOCKS, 0.8f, 1.f);
                    this.markDirty();
                }else {
                    for(int i = 0; i < 5; i++) {
                        this.world.addParticle(ParticleTypes.SPIT, x, y, z, velx, vely, velz);
                    }
                }
            }
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        this.processingStack = ItemStack.fromTag((CompoundTag) tag.get("processingStack"));
        this.processingTimer = tag.getInt("processingTimer");

        ListTag spitList = tag.getList("spitList", 10);
        for(int i = 0; i < spitList.size(); i++){
            CompoundTag compoundTag = spitList.getCompound(i);
            this.spitStacks.add(ItemStack.fromTag(compoundTag));
        }
        this.spitTimer = tag.getInt("spitTimer");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.put("processingStack", this.processingStack.toTag(new CompoundTag()));
        tag.putInt("processingTimer", this.processingTimer);

        ListTag spitList = new ListTag();
        this.spitStacks.forEach((stack) -> {
            spitList.add(10, stack.toTag(new CompoundTag()));
        });
        tag.put("spitList", spitList);
        tag.putInt("spitTimer", this.spitTimer);

        return super.toTag(tag);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int slots[] = { 0 };
        return slots;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}
