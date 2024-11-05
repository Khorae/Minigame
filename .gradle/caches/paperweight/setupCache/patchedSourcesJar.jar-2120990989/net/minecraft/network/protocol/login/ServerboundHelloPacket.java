package net.minecraft.network.protocol.login;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundHelloPacket(String name, UUID profileId) implements Packet<ServerLoginPacketListener> {
    public ServerboundHelloPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(16), buf.readUUID());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.name, 16);
        buf.writeUUID(this.profileId);
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleHello(this);
    }
}
