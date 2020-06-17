package com.github.Crupette.biomechanics.screen;

import com.github.Crupette.biomechanics.block.entity.DigestorBlockEntity;
import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class DigestorScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;

    private final int inventoryStart;
    private final int inventoryEnd;
    private final int hotbarStart;
    private final int hotbarEnd;

    public DigestorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(null, syncId);
        checkSize(inventory, 4);
        checkDataCount(propertyDelegate, 9);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.world;
        this.addSlot(new Slot(inventory, 0, 80, 15) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return super.canInsert(stack) && stack.getItem().isFood();
            }
        });
        this.addSlot(new Slot(inventory, 1, 80, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return super.canInsert(stack) && stack.getItem() == BiomechanicsItems.STOMACH;
            }
        });
        this.addSlot(new Slot(inventory, 2, 80, 55) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return super.canInsert(stack) && stack.getItem() == BiomechanicsItems.SMALL_INTESTINE;
            }
        });
        this.addSlot(new Slot(inventory, 3, 44, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return super.canInsert(stack) && stack.getItem() == BiomechanicsItems.DECAY_STABILIZER;
            }
        });

        this.inventoryStart = this.slots.size();
        int k;
        for(k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
            }
        }
        this.hotbarStart = this.inventoryEnd = this.slots.size();
        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
        this.hotbarEnd = this.slots.size();

        this.addProperties(propertyDelegate);
    }

    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 3) {
                if (!this.insertItem(itemStack2, this.inventoryStart, this.hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onStackChanged(itemStack2, itemStack);
            } else if (index != 0 && index != 1 && index != 2) {
                if (itemStack2.getItem().isFood()) {
                    if (!this.insertItem(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemStack2.getItem().equals(BiomechanicsItems.STOMACH)) {
                    if (!this.insertItem(itemStack2, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemStack2.getItem().equals(BiomechanicsItems.SMALL_INTESTINE)) {
                    if (!this.insertItem(itemStack2, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemStack2.getItem().equals(BiomechanicsItems.DECAY_STABILIZER)) {
                    if (!this.insertItem(itemStack2, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= this.inventoryStart && index < this.inventoryEnd) {
                    if (!this.insertItem(itemStack2, this.hotbarStart, this.hotbarEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= this.hotbarStart && index < this.hotbarEnd && !this.insertItem(itemStack2, this.inventoryStart, this.inventoryEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, this.inventoryStart, this.hotbarEnd, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    @Environment(EnvType.CLIENT)
    public int getStoredCalories() {
        return this.propertyDelegate.get(0);
    }

    @Environment(EnvType.CLIENT)
    public int getStoredMaximum() {
        return this.propertyDelegate.get(1);
    }

    @Environment(EnvType.CLIENT)
    public int getProcessingCalories() {
        return this.propertyDelegate.get(2);
    }

    @Environment(EnvType.CLIENT)
    public int getProcessingMaximum() {
        return this.propertyDelegate.get(3);
    }

    @Environment(EnvType.CLIENT)
    public int getNetworkCalories() {
        return this.propertyDelegate.get(4);
    }

    @Environment(EnvType.CLIENT)
    public int getNetworkCaloriesStorage() {
        return this.propertyDelegate.get(5);
    }

    @Environment(EnvType.CLIENT)
    public int getNetworkCaloriesOverflow() {
        return this.propertyDelegate.get(6);
    }

    @Environment(EnvType.CLIENT)
    public int getNetworkBloodCalories() {
        return this.propertyDelegate.get(7);
    }

    @Environment(EnvType.CLIENT)
    public int getNetworkHeartHealth() {
        return this.propertyDelegate.get(8);
    }
}
