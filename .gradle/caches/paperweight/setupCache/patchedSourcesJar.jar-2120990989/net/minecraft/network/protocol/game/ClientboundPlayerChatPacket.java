package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerChatPacket(
    UUID sender,
    int index,
    @Nullable MessageSignature signature,
    SignedMessageBody.Packed body,
    @Nullable Component unsignedContent,
    FilterMask filterMask,
    ChatType.BoundNetwork chatType
) implements Packet<ClientGamePacketListener> {
    public ClientboundPlayerChatPacket(FriendlyByteBuf buf) {
        this(
            buf.readUUID(),
            buf.readVarInt(),
            buf.readNullable(MessageSignature::read),
            new SignedMessageBody.Packed(buf),
            buf.readNullable(FriendlyByteBuf::readComponentTrusted),
            FilterMask.read(buf),
            new ChatType.BoundNetwork(buf)
        );
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.sender);
        buf.writeVarInt(this.index);
        buf.writeNullable(this.signature, MessageSignature::write);
        this.body.write(buf);
        buf.writeNullable(this.unsignedContent, FriendlyByteBuf::writeComponent);
        FilterMask.write(buf, this.filterMask);
        this.chatType.write(buf);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handlePlayerChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}
