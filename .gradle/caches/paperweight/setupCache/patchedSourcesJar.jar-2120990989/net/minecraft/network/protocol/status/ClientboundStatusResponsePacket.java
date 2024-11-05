package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundStatusResponsePacket(ServerStatus status) implements Packet<ClientStatusPacketListener> {
    public ClientboundStatusResponsePacket(FriendlyByteBuf buf) {
        this(buf.readJsonWithCodec(ServerStatus.CODEC));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(ServerStatus.CODEC, this.status);
    }

    @Override
    public void handle(ClientStatusPacketListener listener) {
        listener.handleStatusResponse(this);
    }
}
