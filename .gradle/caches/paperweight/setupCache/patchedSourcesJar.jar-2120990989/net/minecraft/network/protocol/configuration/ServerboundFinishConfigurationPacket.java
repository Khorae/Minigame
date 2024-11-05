package net.minecraft.network.protocol.configuration;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundFinishConfigurationPacket() implements Packet<ServerConfigurationPacketListener> {
    public ServerboundFinishConfigurationPacket(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(ServerConfigurationPacketListener listener) {
        listener.handleConfigurationFinished(this);
    }

    @Override
    public ConnectionProtocol nextProtocol() {
        return ConnectionProtocol.PLAY;
    }
}
