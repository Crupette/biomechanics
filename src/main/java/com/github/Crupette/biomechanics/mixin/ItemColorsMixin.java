package com.github.Crupette.biomechanics.mixin;


import com.github.Crupette.biomechanics.item.BiomechanicsItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemColors.class)
@Environment(EnvType.CLIENT)
public abstract class ItemColorsMixin {

    @Inject(
            method = "create(Lnet/minecraft/client/color/block/BlockColors;)Lnet/minecraft/client/color/item/ItemColors;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/color/item/ItemColors;register(Lnet/minecraft/client/color/item/ItemColorProvider;[Lnet/minecraft/item/ItemConvertible;)V",
                    ordinal = 3),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addPotionBucketColors(BlockColors blockColors, CallbackInfoReturnable<ItemColors> ci,
                                              ItemColors itemColors){
        itemColors.register((stack, tintIndex) -> {
            int invincible = stack.getOrCreateTag().getInt("invincibleTicks");
            return invincible < 10 ? -1 : 0xFF8888;
        }, BiomechanicsItems.HEART);
        itemColors.register((stack, tintIndex) -> {
            int invincible = stack.getOrCreateTag().getInt("invincibleTicks");
            return invincible < 10 ? -1 : 0xFF8888;
        }, BiomechanicsItems.STOMACH);
        itemColors.register((stack, tintIndex) -> {
            int invincible = stack.getOrCreateTag().getInt("invincibleTicks");
            return invincible < 10 ? -1 : 0xFF8888;
        }, BiomechanicsItems.LUNGS);
    }
}
