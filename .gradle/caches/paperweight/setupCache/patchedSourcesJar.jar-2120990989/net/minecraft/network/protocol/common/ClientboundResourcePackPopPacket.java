package net.minecraft.network.protocol.common;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundResourcePackPopPacket(Optional<UUID> id) implements Packet<ClientCommonPacketListener> {
    public ClientboundResourcePackPopPacket(FriendlyByteBuf buf) {
        this(buf.readOptional(FriendlyByteBuf::readUUID));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeOptional(this.id, FriendlyByteBuf::writeUUID);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleResourcePackPop(this);
    }
}
