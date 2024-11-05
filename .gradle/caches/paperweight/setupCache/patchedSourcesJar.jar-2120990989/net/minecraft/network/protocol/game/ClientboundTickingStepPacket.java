package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.TickRateManager;

public record ClientboundTickingStepPacket(int tickSteps) implements Packet<ClientGamePacketListener> {
    public ClientboundTickingStepPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt());
    }

    public static ClientboundTickingStepPacket from(TickRateManager tickManager) {
        return new ClientboundTickingStepPacket(tickManager.frozenTicksToRun());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.tickSteps);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleTickingStep(this);
    }
}
