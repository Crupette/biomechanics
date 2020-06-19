package com.github.Crupette.biomechanics.network;

import com.github.Crupette.biomechanics.network.UpdateBoilerC2SPacket;

public interface BoilerPacketHandler {
    void onUpdateBoiler(UpdateBoilerC2SPacket packet);
}
