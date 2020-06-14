package com.github.Crupette.biomechanics.item;

import it.unimi.dsi.fastutil.chars.Char2FloatMaps;
import it.unimi.dsi.fastutil.shorts.ShortSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.InventoryProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HeartItem extends Item {
    public HeartItem(Settings settings) {
        super(settings);
    }



    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient) System.out.println("Tick");
        super.inventoryTick(stack, world, entity, slot, selected);

        int suffocationTicks = stack.getOrCreateTag().getInt("suffocationTicks");
        int invincibleTicks = stack.getOrCreateTag().getInt("invincibleTicks");
        int health = stack.getOrCreateTag().getInt("health");

        if(suffocationTicks > 0){
            suffocationTicks--;
        }else{
            if(invincibleTicks > 0){
                invincibleTicks--;
            }else{
                if(entity instanceof Inventory){
                    if(((Inventory)entity).containsAny(Collections.singleton(BiomechanicsItems.DECAY_STABILIZER))){
                        return;
                    }
                }
                if(entity instanceof PlayerEntity){
                    if(((PlayerEntity)entity).inventory.containsAny(Collections.singleton(BiomechanicsItems.DECAY_STABILIZER))){
                        return;
                    }
                }
                entity.playSound(SoundEvents.ENTITY_PLAYER_HURT, 0.8f, (float) ((Math.random() * 0.4) + 0.8));
                if(health <= 0){
                    stack = new ItemStack(Items.ROTTEN_FLESH, 1);
                    if(entity instanceof Inventory){
                        ((Inventory)entity).setStack(slot, stack);
                    }
                    if(entity instanceof PlayerEntity){
                        ((PlayerEntity)entity).inventory.setStack(slot, stack);
                    }
                    return;
                }
                health -= 2;
                invincibleTicks = 20;
            }
        }

        CompoundTag itemTag = stack.getOrCreateTag();

        itemTag.putInt("health", health);
        itemTag.putInt("suffocationTicks", suffocationTicks);
        itemTag.putInt("invincibleTicks", invincibleTicks);

        stack.setTag(itemTag);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        int health = stack.getOrCreateTag().getInt("health");

        tooltip.add(new TranslatableText("tooltip.biomechanics.heart_health").append(" ").append(Integer.toString(health)));
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if(stack.getOrCreateTag().getString("customName") != null){
            return stack.getOrCreateTag().getString("customName") + "'s Heart";
        }
        return super.getTranslationKey(stack) + "." + stack.getOrCreateTag().getString("entity");
    }
}
