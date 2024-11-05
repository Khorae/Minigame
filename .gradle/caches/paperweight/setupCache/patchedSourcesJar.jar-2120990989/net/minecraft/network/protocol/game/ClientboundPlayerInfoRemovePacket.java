package net.minecraft.network.protocol.game;

import java.util.List;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerInfoRemovePacket(List<UUID> profileIds) implements Packet<ClientGamePacketListener> {
    public ClientboundPlayerInfoRemovePacket(FriendlyByteBuf buf) {
        this(buf.readList(FriendlyByteBuf::readUUID));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(this.profileIds, FriendlyByteBuf::writeUUID);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handlePlayerInfoRemove(this);
    }
}
