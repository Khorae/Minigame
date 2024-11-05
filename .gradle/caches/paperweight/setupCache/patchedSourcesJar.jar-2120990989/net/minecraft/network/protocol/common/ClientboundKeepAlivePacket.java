package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundKeepAlivePacket implements Packet<ClientCommonPacketListener> {
    private final long id;

    public ClientboundKeepAlivePacket(long id) {
        this.id = id;
    }

    public ClientboundKeepAlivePacket(FriendlyByteBuf buf) {
        this.id = buf.readLong();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeLong(this.id);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleKeepAlive(this);
    }

    public long getId() {
        return this.id;
    }
}
