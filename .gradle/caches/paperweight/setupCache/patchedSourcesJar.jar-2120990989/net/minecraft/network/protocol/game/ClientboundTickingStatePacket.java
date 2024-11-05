package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.TickRateManager;

public record ClientboundTickingStatePacket(float tickRate, boolean isFrozen) implements Packet<ClientGamePacketListener> {
    public ClientboundTickingStatePacket(FriendlyByteBuf buf) {
        this(buf.readFloat(), buf.readBoolean());
    }

    public static ClientboundTickingStatePacket from(TickRateManager tickManager) {
        return new ClientboundTickingStatePacket(tickManager.tickrate(), tickManager.isFrozen());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(this.tickRate);
        buf.writeBoolean(this.isFrozen);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleTickingState(this);
    }
}
