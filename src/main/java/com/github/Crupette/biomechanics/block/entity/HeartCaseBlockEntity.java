package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import com.github.Crupette.biomechanics.item.HeartItem;
import com.github.Crupette.biomechanics.screen.HeartCaseScreenHandler;
import com.github.Crupette.biomechanics.util.tree.GenericTree;
import com.github.Crupette.biomechanics.util.tree.GenericTreeNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.World;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class HeartCaseBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable, Biological {
    private static final int[] TOP_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] BOTTOM_SLOTS = new int[]{4, 5};
    private static final int[] SIDE_SLOTS = new int[]{1, 2, 3};
    protected DefaultedList<ItemStack> inventory;

    private int depletedBottles = 0;
    private int saturatedBottles = 0;
    private int bpm = 0;
    private int calories = 0;
    private int oxygen = 0;

    private int saturatedBottlesNeeded = 1;
    private int depletedBottlesNeeded = 1;

    private boolean needsTree = true;

    private final List<BlockPos> connected = new ArrayList<>();
    private final GenericTree<BlockPos> connectionTree;

    protected final PropertyDelegate propertyDelegate;

    public HeartCaseBlockEntity() {
        super(BiomechanicsBlockEntities.HEART_CASE);

        this.inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index) {
                    case 0: return HeartCaseBlockEntity.this.depletedBottles;
                    case 1: return HeartCaseBlockEntity.this.saturatedBottles;
                    case 2: return HeartCaseBlockEntity.this.bpm;
                    case 3: return HeartCaseBlockEntity.this.calories;
                    case 4: return HeartCaseBlockEntity.this.saturatedBottlesNeeded;
                    case 5: return HeartCaseBlockEntity.this.depletedBottlesNeeded;
                    default:
                        return 0;
                }
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0: HeartCaseBlockEntity.this.depletedBottles = value;
                        break;
                    case 1: HeartCaseBlockEntity.this.saturatedBottles = value;
                        break;
                    case 2: HeartCaseBlockEntity.this.bpm = value;
                        break;
                    case 3: HeartCaseBlockEntity.this.calories = value;
                        break;
                    case 4: HeartCaseBlockEntity.this.saturatedBottlesNeeded = value;
                        break;
                    case 5: HeartCaseBlockEntity.this.depletedBottlesNeeded = value;
                        break;
                }

            }

            @Override
            public int size() {
                return 6;
            }
        };

        this.connectionTree = new GenericTree<>();
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.biomechanics.heart_case");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HeartCaseScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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
        if (dir == Direction.DOWN && slot >= 4) return true;
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
        if (slot == 4 || slot == 5) {
            return false;
        } else if(slot == 0 && stack.getItem() instanceof HeartItem){
            return true;
        } else if(slot == 1 && stack.getItem() == BiomechanicsItems.DECAY_STABILIZER){
            return true;
        }else{
            return stack.getItem() == BiomechanicsItems.BLOOD_BOTTLE;
        }
    }

    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void tick() {
        boolean dirty = false;
        if(this.needsTree){
            this.needsTree = false;
            this.buildConnectionTree();
        }

        if(!world.isClient){
            ItemStack heartStack = this.inventory.get(0);
            if(!heartStack.isEmpty()) {
                if (this.saturatedBottles < this.saturatedBottlesNeeded || this.depletedBottles < this.depletedBottlesNeeded || this.calories == 0) {
                    int suffocationTicks = heartStack.getOrCreateTag().getInt("suffocationTicks");
                    int invincibleTicks = heartStack.getOrCreateTag().getInt("invincibleTicks");
                    int health = heartStack.getOrCreateTag().getInt("health");

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
                        CompoundTag tag = heartStack.getTag();
                        tag.putInt("suffocationTicks", suffocationTicks);
                        tag.putInt("invincibleTicks", invincibleTicks);
                        tag.putInt("health", health);

                        heartStack.setTag(tag);
                    }
                }
            }

            for(int slot = 2; slot < 4; slot++){
                ItemStack bottleSlot = this.inventory.get(slot);
                if(!bottleSlot.isEmpty()){
                    if(bottleSlot.getItem() == BiomechanicsItems.BLOOD_BOTTLE){
                        ItemStack outputStack = this.inventory.get(slot == 2 ? 4 : 5);
                        if(outputStack.isEmpty() || (outputStack.getItem() == Items.GLASS_BOTTLE && outputStack.getCount() < outputStack.getMaxCount())) {
                            int outSlot = slot + 2;
                            int fluidCount = slot == 2 ? this.saturatedBottles : this.depletedBottles;
                            int fluidNeeded = slot == 2 ? this.saturatedBottlesNeeded : this.depletedBottlesNeeded;

                            if(fluidCount < fluidNeeded){
                                fluidCount++;
                                bottleSlot.decrement(1);
                                if(outputStack.isEmpty()){
                                    outputStack = new ItemStack(Items.GLASS_BOTTLE);
                                    this.inventory.set(outSlot, outputStack);
                                }else{
                                    outputStack.increment(1);
                                }
                                dirty = true;
                            }

                            if(slot == 2) this.saturatedBottles = fluidCount;
                            if(slot == 3) this.depletedBottles = fluidCount;
                        }
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

        this.saturatedBottles = tag.getShort("saturatedBottles");
        this.depletedBottles = tag.getShort("depletedBottles");
        this.bpm = tag.getShort("bpm");
        this.calories = tag.getInt("calories");
        this.oxygen = tag.getInt("oxygen");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        Inventories.toTag(tag, this.inventory);

        tag.putShort("saturatedBottles", (short) this.saturatedBottles);
        tag.putShort("depletedBottles", (short) this.depletedBottles);
        tag.putShort("bpm", (short) this.bpm);
        tag.putInt("calories", this.calories);
        tag.putInt("oxygen", this.oxygen);

        return tag;
    }

    @Override
    public BlockPos getParent() {
        return this.pos;
    }

    @Override
    public int getCalorieCost() {
        return 1;
    }

    @Override
    public int getOxygenCost() {
        return 2;
    }

    private GenericTreeNode<BlockPos> getNode(BlockPos pos, GenericTreeNode<BlockPos> root){
        if(root.getData().equals(pos)) return null;
        return this.connectionTree.find(pos);
    }

    private boolean addConnection(BlockPos pos, GenericTreeNode<BlockPos> root, BlockPos origin){
        if(this.world == null) return false;
        if(this.world.getBlockEntity(pos) == null) return false;
        if(!(this.world.getBlockEntity(pos) instanceof Biological)) return false;

        GenericTreeNode<BlockPos> newNode = new GenericTreeNode<>(pos);

        if(this.connected.contains(pos)) return false;
        this.connected.add(pos);

        boolean connected = false;
        System.out.println("From " + pos + ": ");
        for(Direction direction : Direction.values()){
            BlockPos newPos = pos.mutableCopy().add(direction.getVector());
            System.out.println( "     to " + newPos + " (origin " + origin + ")");
            if(origin.equals(newPos) && direction == Direction.DOWN){
                System.out.println("Connecting back to parent at " + newPos);
                root.addChild(newNode);
                return true;
            }
            if(origin.equals(newPos)) continue;
            if(this.connected.contains(newPos)){
                GenericTreeNode<BlockPos> connection = getNode(newPos, root);
                System.out.println("Checking back at " + newPos + " : " + connection);
                printTree(0, root);
                if(connection == null) continue;
                System.out.println("Found pre-existing path back @ " + newPos);
                root.addChild(newNode);
                newNode.addChild(connection);
                return true;
            }
            if(this.addConnection(newPos, newNode, origin)){
                if(!connected){
                    root.addChild(newNode);
                }
                connected = true;
                System.out.println("Reached back for continued checks at branch " + pos);
            }
        }
        return connected;
    }

    private void printTree(int depth, GenericTreeNode<BlockPos> node){
        StringBuilder msg = new StringBuilder("");
        for(int i = 0; i < depth; i++) msg.append(" ");
        msg.append(node.getData().toShortString());
        System.out.println(msg.toString());
        node.getChildren().forEach((child) -> {
            printTree(depth + 1, child);
        });
    }

    public void buildConnectionTree(){
        this.connected.clear();
        this.connected.add(this.pos);
        this.connectionTree.setRoot(new GenericTreeNode<>(this.pos));

        if(!this.addConnection(this.pos.add(0, -1, 0), this.connectionTree.getRoot(), this.pos)){
            System.out.println("Circulatory system is not contained!");
            this.connected.clear();
            this.connectionTree.setRoot(null);
        }else{
            System.out.println("Found loop for system");
            printTree(0, this.connectionTree.getRoot());
        }
    }
}
