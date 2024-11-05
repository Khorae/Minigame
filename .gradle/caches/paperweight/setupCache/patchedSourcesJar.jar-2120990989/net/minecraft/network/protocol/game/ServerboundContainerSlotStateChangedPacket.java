package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundContainerSlotStateChangedPacket(int slotId, int containerId, boolean newState) implements Packet<ServerGamePacketListener> {
    public ServerboundContainerSlotStateChangedPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.slotId);
        buf.writeVarInt(this.containerId);
        buf.writeBoolean(this.newState);
    }

    @Override
    public void handle(ServerGamePacketListener listener) {
        listener.handleContainerSlotStateChanged(this);
    }
}
