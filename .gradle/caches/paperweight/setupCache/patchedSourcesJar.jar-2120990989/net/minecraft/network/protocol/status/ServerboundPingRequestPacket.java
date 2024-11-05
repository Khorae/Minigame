package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerPingPacketListener;

public class ServerboundPingRequestPacket implements Packet<ServerPingPacketListener> {
    private final long time;

    public ServerboundPingRequestPacket(long startTime) {
        this.time = startTime;
    }

    public ServerboundPingRequestPacket(FriendlyByteBuf buf) {
        this.time = buf.readLong();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeLong(this.time);
    }

    @Override
    public void handle(ServerPingPacketListener listener) {
        listener.handlePingRequest(this);
    }

    public long getTime() {
        return this.time;
    }
}
