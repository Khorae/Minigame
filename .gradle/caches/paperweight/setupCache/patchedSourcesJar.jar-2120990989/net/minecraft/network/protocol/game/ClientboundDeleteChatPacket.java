package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;

public record ClientboundDeleteChatPacket(MessageSignature.Packed messageSignature) implements Packet<ClientGamePacketListener> {
    public ClientboundDeleteChatPacket(FriendlyByteBuf buf) {
        this(MessageSignature.Packed.read(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        MessageSignature.Packed.write(buf, this.messageSignature);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleDeleteChat(this);
    }
}
