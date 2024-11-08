package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;

public class ClientboundBundlePacket extends BundlePacket<ClientGamePacketListener> {
    public ClientboundBundlePacket(Iterable<Packet<ClientGamePacketListener>> packets) {
        super(packets);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleBundlePacket(this);
    }
}
