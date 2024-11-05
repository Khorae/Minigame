package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatCommandPacket(
    String command, Instant timeStamp, long salt, ArgumentSignatures argumentSignatures, LastSeenMessages.Update lastSeenMessages
) implements Packet<ServerGamePacketListener> {
    public ServerboundChatCommandPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(256), buf.readInstant(), buf.readLong(), new ArgumentSignatures(buf), new LastSeenMessages.Update(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.command, 256);
        buf.writeInstant(this.timeStamp);
        buf.writeLong(this.salt);
        this.argumentSignatures.write(buf);
        this.lastSeenMessages.write(buf);
    }

    @Override
    public void handle(ServerGamePacketListener listener) {
        listener.handleChatCommand(this);
    }
}
