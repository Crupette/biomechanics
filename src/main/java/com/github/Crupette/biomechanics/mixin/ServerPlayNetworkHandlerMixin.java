package com.github.Crupette.biomechanics.mixin;

import com.github.Crupette.biomechanics.network.UpdateBoilerC2SPacket;
import com.github.Crupette.biomechanics.screen.BoilerScreenHandler;
import com.github.Crupette.biomechanics.network.BoilerPacketHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener, BoilerPacketHandler {

    @Shadow
    public ServerPlayerEntity player;

    @Override
    public void onUpdateBoiler(UpdateBoilerC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (this.player.currentScreenHandler instanceof BoilerScreenHandler) {
            ((BoilerScreenHandler)this.player.currentScreenHandler).setCalorieCost(packet.getCalorieCost());
        }
    }
}
