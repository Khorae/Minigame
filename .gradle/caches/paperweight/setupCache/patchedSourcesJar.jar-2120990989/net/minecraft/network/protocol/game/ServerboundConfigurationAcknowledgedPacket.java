package net.minecraft.network.protocol.game;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundConfigurationAcknowledgedPacket() implements Packet<ServerGamePacketListener> {
    public ServerboundConfigurationAcknowledgedPacket(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(ServerGamePacketListener listener) {
        listener.handleConfigurationAcknowledged(this);
    }

    @Override
    public ConnectionProtocol nextProtocol() {
        return ConnectionProtocol.CONFIGURATION;
    }
}
