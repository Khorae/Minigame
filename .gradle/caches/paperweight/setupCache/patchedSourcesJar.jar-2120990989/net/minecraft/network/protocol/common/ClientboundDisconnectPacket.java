package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundDisconnectPacket implements Packet<ClientCommonPacketListener> {
    private final Component reason;

    public ClientboundDisconnectPacket(Component reason) {
        this.reason = reason;
    }

    public ClientboundDisconnectPacket(FriendlyByteBuf buf) {
        this.reason = buf.readComponentTrusted();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeComponent(this.reason);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleDisconnect(this);
    }

    public Component getReason() {
        return this.reason;
    }
}
