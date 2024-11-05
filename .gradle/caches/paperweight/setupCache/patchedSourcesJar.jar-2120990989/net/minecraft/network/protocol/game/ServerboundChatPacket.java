package net.minecraft.network.protocol.game;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatPacket(String message, Instant timeStamp, long salt, @Nullable MessageSignature signature, LastSeenMessages.Update lastSeenMessages)
    implements Packet<ServerGamePacketListener> {
    public ServerboundChatPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(256), buf.readInstant(), buf.readLong(), buf.readNullable(MessageSignature::read), new LastSeenMessages.Update(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.message, 256);
        buf.writeInstant(this.timeStamp);
        buf.writeLong(this.salt);
        buf.writeNullable(this.signature, MessageSignature::write);
        this.lastSeenMessages.write(buf);
    }

    @Override
    public void handle(ServerGamePacketListener listener) {
        listener.handleChat(this);
    }
}
