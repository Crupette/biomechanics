package com.github.Crupette.biomechanics.mixin;

import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
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

    @Shadow public abstract float getMaxHealth();

    @Shadow public abstract boolean isUndead();

    @Shadow public abstract boolean isMobOrPlayer();

    @Shadow public abstract LivingEntity getAttacker();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "drop", at = @At("TAIL"))
    private void dropOrgans(DamageSource source, CallbackInfo ci){
        if(this.getAttacker() == null) return;
        if(this.getAttacker().getAttacking() == null) return;

        LivingEntity livingEntity = this.getAttacker().getAttacking();
        if(this.isUndead()) return;
        if(livingEntity instanceof IronGolemEntity) return;

        Entity sourceEntity = source.getSource();
        if(!(sourceEntity instanceof PlayerEntity)){
            return;
        }
        if(!((PlayerEntity) sourceEntity).getEquippedStack(EquipmentSlot.OFFHAND).getItem().equals(BiomechanicsItems.SCALPEL) &&
                !((PlayerEntity) sourceEntity).getEquippedStack(EquipmentSlot.MAINHAND).getItem().equals(BiomechanicsItems.SCALPEL)) return;
        if(!this.world.isClient && this.isMobOrPlayer()){
            ItemStack heartStack = new ItemStack(BiomechanicsItems.HEART, 1);
            ItemStack stomachStack = new ItemStack(BiomechanicsItems.STOMACH, 1);
            ItemStack lungsStack = new ItemStack(BiomechanicsItems.LUNGS, 1);
            ItemStack smallIntestineStack = new ItemStack(BiomechanicsItems.SMALL_INTESTINE, 1);

            CompoundTag tag = new CompoundTag();
            tag.putInt("health", (int)this.getMaxHealth());
            tag.putInt("suffocationTicks", 200);
            tag.putInt("invincibleTicks", 0);
            tag.putString("entity", Registry.ENTITY_TYPE.getId(this.getType()).getPath());
            if(this.hasCustomName()){
                tag.putString("customName", this.getCustomName().getString());
            }
            heartStack.setTag(tag);
            stomachStack.setTag(tag);
            lungsStack.setTag(tag);
            smallIntestineStack.setTag(tag);

            this.world.spawnEntity(new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), heartStack));
            this.world.spawnEntity(new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), stomachStack));
            this.world.spawnEntity(new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), lungsStack));
            this.world.spawnEntity(new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), smallIntestineStack));
        }
    }
}
