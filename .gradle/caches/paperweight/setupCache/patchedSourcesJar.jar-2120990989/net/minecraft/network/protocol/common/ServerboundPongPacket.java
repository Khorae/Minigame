package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPongPacket implements Packet<ServerCommonPacketListener> {
    private final int id;

    public ServerboundPongPacket(int parameter) {
        this.id = parameter;
    }

    public ServerboundPongPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handlePong(this);
    }

    public int getId() {
        return this.id;
    }
}
