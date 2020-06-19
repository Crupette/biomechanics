package com.github.Crupette.biomechanics.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

import java.io.IOException;

public class UpdateBoilerC2SPacket implements Packet<ServerPlayPacketListener> {
    private int calorieCost;

    public UpdateBoilerC2SPacket(){

    }

    @Environment(EnvType.CLIENT)
    public UpdateBoilerC2SPacket(int calorieCost){
        this.calorieCost = calorieCost;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.calorieCost = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.calorieCost);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ((BoilerPacketHandler)listener).onUpdateBoiler(this);
    }

    public int getCalorieCost() { return this.calorieCost; }
}
