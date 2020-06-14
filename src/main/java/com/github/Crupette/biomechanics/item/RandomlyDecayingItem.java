package com.github.Crupette.biomechanics.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RandomlyDecayingItem extends Item {
    public RandomlyDecayingItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        int random = (int) (Math.random() * 80);
        if(random == 1 && entity instanceof LivingEntity){
            stack.damage(1, (LivingEntity)entity, (e) -> {
            });
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
