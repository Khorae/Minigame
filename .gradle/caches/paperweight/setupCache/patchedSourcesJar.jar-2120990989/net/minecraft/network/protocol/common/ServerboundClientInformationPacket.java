package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ClientInformation;

public record ServerboundClientInformationPacket(ClientInformation information) implements Packet<ServerCommonPacketListener> {
    public ServerboundClientInformationPacket(FriendlyByteBuf buf) {
        this(new ClientInformation(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.information.write(buf);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleClientInformation(this);
    }
}
