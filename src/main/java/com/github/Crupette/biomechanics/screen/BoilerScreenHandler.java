package com.github.Crupette.biomechanics.screen;

import com.github.Crupette.biomechanics.tag.BiomechanicsTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class BoilerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;

    private final int inventoryStart;
    private final int inventoryEnd;
    private final int hotbarStart;
    private final int hotbarEnd;

    public BoilerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(BiomechanicsScreenTypes.BOILER, syncId);
        checkSize(inventory, 2);
        checkDataCount(propertyDelegate, 3);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.world;
        this.addSlot(new Slot(inventory, 0, 56, 35));
        this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 1, 107, 35));

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

    public BoilerScreenHandler(int sync, PlayerInventory playerInventory) {
        this(sync, playerInventory, new SimpleInventory(2), new ArrayPropertyDelegate(3));
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
            if (index == 1) {
                if (!this.insertItem(itemStack2, this.inventoryStart, this.hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onStackChanged(itemStack2, itemStack);
            } else if (index != 0) {
                if (!this.insertItem(itemStack2, 0, 1, false)) {
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
    public int getBoilProgress() {
        int i = this.propertyDelegate.get(0);
        int j = this.propertyDelegate.get(1);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    @Environment(EnvType.CLIENT)
    public int getCalorieCost(){
        return this.propertyDelegate.get(2);
    }

    public void setCalorieCost(int cost){
        this.propertyDelegate.set(2, cost);
    }
}
