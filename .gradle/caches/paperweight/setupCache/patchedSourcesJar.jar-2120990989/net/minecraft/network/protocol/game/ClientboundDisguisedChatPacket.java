package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public record ClientboundDisguisedChatPacket(Component message, ChatType.BoundNetwork chatType) implements Packet<ClientGamePacketListener> {
    public ClientboundDisguisedChatPacket(FriendlyByteBuf buf) {
        this(buf.readComponentTrusted(), new ChatType.BoundNetwork(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeComponent(this.message);
        this.chatType.write(buf);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleDisguisedChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}
