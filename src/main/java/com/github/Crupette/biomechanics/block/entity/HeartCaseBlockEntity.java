package com.github.Crupette.biomechanics.block.entity;

import com.github.Crupette.biomechanics.Biomechanics;
import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import com.github.Crupette.biomechanics.screen.HeartCaseScreenHandler;
import com.github.Crupette.biomechanics.util.network.CirculatoryNetwork;
import com.github.Crupette.biomechanics.util.tree.GenericTree;
import com.github.Crupette.biomechanics.util.tree.GenericTreeNode;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockEntityProvider;
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

public class HeartCaseBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, SidedInventory, Tickable, BiologicalNetworked {
    private static final int[] TOP_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] BOTTOM_SLOTS = new int[]{4, 5};
    private static final int[] SIDE_SLOTS = new int[]{1, 2, 3};
    protected DefaultedList<ItemStack> inventory;
    public CirculatoryNetwork network;

    private int depletedBottles = 0;
    private int saturatedBottles = 0;
    private int heartAttackChance = 0;
    private short beatTick = 0;
    private boolean requestsBeat = true;
    private boolean heartAttack = false;

    private int saturatedBottlesNeeded = 0;
    private int depletedBottlesNeeded = 0;

    private boolean needsTree = true;

    private final List<BlockPos> connected = new ArrayList<>();
    private final GenericTree<Connection> connectionTree;

    protected final PropertyDelegate propertyDelegate;

    public HeartCaseBlockEntity() {
        super(BiomechanicsBlockEntities.HEART_CASE);

        this.inventory = DefaultedList.ofSize(7, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index) {
                    case 0: return HeartCaseBlockEntity.this.depletedBottles;
                    case 1: return HeartCaseBlockEntity.this.saturatedBottles;
                    case 2: return HeartCaseBlockEntity.this.heartAttack ? 1 : 0;
                    case 3: return HeartCaseBlockEntity.this.saturatedBottlesNeeded;
                    case 4: return HeartCaseBlockEntity.this.depletedBottlesNeeded;
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
                    case 2: HeartCaseBlockEntity.this.heartAttack = value > 0;
                        break;
                    case 3: HeartCaseBlockEntity.this.saturatedBottlesNeeded = value;
                        break;
                    case 4: HeartCaseBlockEntity.this.depletedBottlesNeeded = value;
                        break;
                }

            }

            @Override
            public int size() {
                return 5;
            }
        };

        this.connectionTree = new GenericTree<>();
        this.network = new CirculatoryNetwork(this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Biomechanics.getTranslated("container", "heart_case");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new HeartCaseScreenHandler(syncId, inv, this, this.propertyDelegate);
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
        if(slot == 0){
            this.heartAttack = false;
            this.beatTick = 0;
            this.requestsBeat = false;
        }
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
        } else if(slot == 0 && stack.getItem().equals(BiomechanicsItems.HEART)){
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

    @Override
    public void tick() {
        boolean dirty = false;

        if(this.needsTree){
            this.needsTree = false;
            this.updateConnectionTree();
        }

        if(!world.isClient){
            ItemStack heartStack = this.inventory.get(0);

            if(!heartStack.isEmpty()) {
                ItemStack organStack = this.inventory.get(0);

                int heartHealth = organStack.getOrCreateTag().getInt("health");

                int sustainOxygen = this.network.requestOxygen(1);
                int sustainCalories = this.network.requestOxygen(1);
                if (this.saturatedBottles < this.saturatedBottlesNeeded || this.depletedBottles < this.depletedBottlesNeeded || sustainOxygen < 1 || sustainCalories < 1) {
                    damageOrgan();
                }else{
                    healOrgan();
                }

                if(this.beatTick > 0) {
                    this.beatTick--;
                    if(this.beatTick == 5){
                        this.world.playSound(null, this.pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.4f, 1.3f);
                    }
                }

                if((this.network.getBloodCalories() < heartHealth * 10 || this.network.getBloodOxygen() < heartHealth * 10) && this.beatTick == 0){
                    this.requestsBeat = true;
                }

                if(this.requestsBeat && !this.heartAttack){
                    sustainOxygen = this.network.requestOxygen(4);
                    sustainCalories = this.network.requestCalories(2);

                    this.requestsBeat = false;

                    if(this.beatTick > heartHealth * 2 && this.inventory.get(1).isEmpty()){
                        this.heartAttack = true;
                    }
                    int haChance = (int)((float)this.network.getCalorieOverflow() / 10000);
                    if(haChance > 0){
                        if(Math.random() * 100 > haChance) this.heartAttack = true;
                    }
                    if(sustainCalories >= 4 && sustainOxygen >= 2) {
                        this.network.onBeat(heartHealth);
                        this.beatTick = 5;
                        this.world.playSound(null, this.pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.4f, 0.5f);
                    }else{
                        this.network.onBeat(heartHealth / 2);
                        this.beatTick += 20;
                        this.world.playSound(null, this.pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.6f, 0.5f);
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
                            int fluidCount = slot == 3 ? this.saturatedBottles : this.depletedBottles;
                            int fluidNeeded = slot == 3 ? this.saturatedBottlesNeeded : this.depletedBottlesNeeded;

                            if(fluidCount < fluidNeeded){
                                this.network.provideCalories(1000);
                                this.network.provideOxygen(1000);
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

                            if(slot == 3) this.saturatedBottles = fluidCount;
                            if(slot == 2) this.depletedBottles = fluidCount;
                        }
                    }
                }
            }
            if(dirty){
                this.markDirty();
            }
        }
    }

    public void requestBeat() { this.requestsBeat = true; }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);

        this.saturatedBottles = tag.getShort("saturatedBottles");
        this.depletedBottles = tag.getShort("depletedBottles");
        this.requestsBeat = tag.getBoolean("needsBeat");
        this.beatTick = tag.getShort("beatTick");

        this.network.fromTag(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        Inventories.toTag(tag, this.inventory);

        tag.putShort("saturatedBottles", (short) this.saturatedBottles);
        tag.putShort("depletedBottles", (short) this.depletedBottles);
        tag.putBoolean("needsBeat", this.requestsBeat);
        tag.putShort("beatTick", this.beatTick);

        this.network.toTag(tag);

        return tag;
    }

    @Override
    public BlockPos getParent() {
        return this.pos;
    }

    @Override
    public void setParent(BlockPos pos) {
    }

    @Override
    public int getCalorieStorageCapacity() {
        return 8192;
    }

    @Override
    public void onBeat() {

    }

    private void setConnections(BlockPos to){
        this.connected.forEach((blockPos -> {
            BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
            if(blockEntity != null){
                if(blockEntity instanceof Biological){
                    ((Biological)blockEntity).setParent(to);
                }
                if(blockEntity instanceof BiologicalAdjacent){
                    ((BiologicalAdjacent)blockEntity).setParent(to);
                }
            }
        }));
    }

    private GenericTreeNode<Connection> findNode(GenericTreeNode<Connection> node, BlockPos pos, GenericTreeNode<Connection> limiter){
        for(GenericTreeNode<Connection> child : node.getChildren()){
            if(child.getData().pos.equals(pos)) return child;
            GenericTreeNode<Connection> found = findNode(child, pos, limiter);

            if(found != null) return found;
        }
        return null;
    }

    private void printTree(GenericTreeNode<Connection> node, int depth){
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < depth; i++) sb.append(" ");
        sb.append(node.getData().pos + " : " + node.getData().flow);
        for(GenericTreeNode<Connection> child : node.getChildren()){
            printTree(child, depth + 1);
        }
    }

    private boolean addConnection(BlockPos pos, GenericTreeNode<Connection> root, GenericTreeNode<Connection> limiter, Direction fromDir){
        if(this.world == null) return false;
        BlockEntity check = this.world.getBlockEntity(pos);
        if(check == null) return false;
        if(!(check instanceof BiologicalNetworked)) return false;

        if(((Biological)check).getParent() != null && ((Biological)check).getParent() != this.pos) return false;

        boolean connected = false;
        GenericTreeNode<Connection> newConnection = new GenericTreeNode<>(new Connection(pos));
        newConnection.getData().flow.add(fromDir);

        root.addChild(newConnection);
        this.connected.add(pos);

        for(Direction direction : Direction.values()){
            BlockPos newPos = pos.add(direction.getVector());

            boolean breakOut = false;
            for(Direction flow : newConnection.getData().flow){
                if(flow.getOpposite().equals(direction)) {
                    breakOut = true;
                    break;
                }
            }
            if(breakOut) {
                continue;
            }

            if(this.pos.equals(newPos) && direction == Direction.DOWN){
                newConnection.getData().flow.add(direction);
                return true;
            }
            if(this.pos.equals(newPos)) continue;
            if(this.findNode(this.connectionTree.getRoot(), newPos, limiter) != null) continue;

            if(this.addConnection(newPos, newConnection, limiter, direction)){
                newConnection.getData().flow.add(direction);
                connected = true;
                limiter = newConnection;
            }
        }

        if(!connected){
            this.connected.remove(pos);
            root.removeChild(newConnection);
        }
        return connected;
    }

    public void updateConnectionTree(){
        this.connected.clear();
        this.network.reset();
        this.connectionTree.setRoot(new GenericTreeNode<>(new Connection(this.pos, Direction.DOWN)));

        this.saturatedBottlesNeeded = 1;
        this.depletedBottlesNeeded = 1;

        if(this.addConnection(this.pos.down(), this.connectionTree.getRoot(), null, Direction.DOWN)){
            for(BlockPos pos : this.connected){
                this.saturatedBottlesNeeded++;
                this.depletedBottlesNeeded++;

                this.network.addChild(this.world.getBlockEntity(pos));

                for(Direction direction : Direction.values()){
                    BlockPos newPos = pos.add(direction.getVector());
                    BlockEntity blockEntity = this.world.getBlockEntity(newPos);
                    if(blockEntity instanceof BiologicalAdjacent){
                        this.network.addChild(blockEntity);
                        ((BiologicalAdjacent)blockEntity).setParent(this.pos);
                    }
                }
            }
            this.setConnections(this.pos);
        }else{
            this.connected.clear();
            this.connectionTree.setRoot(null);
        }

        if(this.saturatedBottles > this.saturatedBottlesNeeded) this.saturatedBottles = this.saturatedBottlesNeeded;
        if(this.depletedBottles > this.depletedBottlesNeeded) this.depletedBottles = this.depletedBottlesNeeded;
    }

    public void nullifyConnections() {
        setConnections(null);
    }

    private static class Connection {
        public BlockPos pos;
        public List<Direction> flow = new ArrayList<>();

        public Connection(BlockPos pos, Direction flow){
            this.pos = pos;
            this.flow.add(flow);
        }

        public Connection(BlockPos pos){
            this.pos = pos;
        }
    }
}
