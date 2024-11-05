package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundKeepAlivePacket implements Packet<ServerCommonPacketListener> {
    private final long id;

    public ServerboundKeepAlivePacket(long id) {
        this.id = id;
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleKeepAlive(this);
    }

    public ServerboundKeepAlivePacket(FriendlyByteBuf buf) {
        this.id = buf.readLong();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeLong(this.id);
    }

    public long getId() {
        return this.id;
    }
}
