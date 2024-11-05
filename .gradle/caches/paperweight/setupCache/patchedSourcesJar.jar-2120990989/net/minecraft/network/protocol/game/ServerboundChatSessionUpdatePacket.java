package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatSessionUpdatePacket(RemoteChatSession.Data chatSession) implements Packet<ServerGamePacketListener> {
    public ServerboundChatSessionUpdatePacket(FriendlyByteBuf buf) {
        this(RemoteChatSession.Data.read(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        RemoteChatSession.Data.write(buf, this.chatSession);
    }

    @Override
    public void handle(ServerGamePacketListener listener) {
        listener.handleChatSessionUpdate(this);
    }
}
