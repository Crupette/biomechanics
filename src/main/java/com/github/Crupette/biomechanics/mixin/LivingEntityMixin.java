package com.github.Crupette.biomechanics.mixin;

import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow @Final public int defaultMaxHealth;

    @Shadow public abstract float getMaxHealth();

    @Shadow public abstract boolean isUndead();

    @Shadow public abstract boolean isMobOrPlayer();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "drop", at = @At("TAIL"))
    private void dropOrgans(DamageSource source, CallbackInfo ci){
        if(this.isUndead()) return;
        if(!this.world.isClient && this.isMobOrPlayer()){
            ItemStack heartStack = new ItemStack(BiomechanicsItems.HEART, 1);
            CompoundTag tag = new CompoundTag();
            tag.putInt("health", (int)this.getMaxHealth());
            tag.putInt("suffocationTicks", 200);
            tag.putInt("invincibleTicks", 0);
            tag.putString("entity", Registry.ENTITY_TYPE.getId(this.getType()).getPath());
            if(this.hasCustomName()){
                tag.putString("customName", this.getCustomName().getString());
            }
            heartStack.setTag(tag);

            this.world.spawnEntity(new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), heartStack));
        }
    }
}
