package net.minecraft.network.protocol.common;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public record ClientboundResourcePackPushPacket(UUID id, String url, String hash, boolean required, @Nullable Component prompt)
    implements Packet<ClientCommonPacketListener> {
    public static final int MAX_HASH_LENGTH = 40;

    public ClientboundResourcePackPushPacket(UUID id, String url, String hash, boolean required, @Nullable Component prompt) {
        if (hash.length() > 40) {
            throw new IllegalArgumentException("Hash is too long (max 40, was " + hash.length() + ")");
        } else {
            this.id = id;
            this.url = url;
            this.hash = hash;
            this.required = required;
            this.prompt = prompt;
        }
    }

    public ClientboundResourcePackPushPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readUtf(), buf.readUtf(40), buf.readBoolean(), buf.readNullable(FriendlyByteBuf::readComponentTrusted));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.id);
        buf.writeUtf(this.url);
        buf.writeUtf(this.hash);
        buf.writeBoolean(this.required);
        buf.writeNullable(this.prompt, FriendlyByteBuf::writeComponent);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleResourcePackPush(this);
    }
}
