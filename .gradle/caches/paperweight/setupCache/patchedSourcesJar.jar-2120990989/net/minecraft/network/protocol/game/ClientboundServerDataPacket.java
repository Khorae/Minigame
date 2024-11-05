package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundServerDataPacket implements Packet<ClientGamePacketListener> {
    private final Component motd;
    private final Optional<byte[]> iconBytes;
    private final boolean enforcesSecureChat;

    public ClientboundServerDataPacket(Component description, Optional<byte[]> favicon, boolean previewsChat) {
        this.motd = description;
        this.iconBytes = favicon;
        this.enforcesSecureChat = previewsChat;
    }

    public ClientboundServerDataPacket(FriendlyByteBuf buf) {
        this.motd = buf.readComponentTrusted();
        this.iconBytes = buf.readOptional(FriendlyByteBuf::readByteArray);
        this.enforcesSecureChat = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeComponent(this.motd);
        buf.writeOptional(this.iconBytes, FriendlyByteBuf::writeByteArray);
        buf.writeBoolean(this.enforcesSecureChat);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleServerData(this);
    }

    public Component getMotd() {
        return this.motd;
    }

    public Optional<byte[]> getIconBytes() {
        return this.iconBytes;
    }

    public boolean enforcesSecureChat() {
        return this.enforcesSecureChat;
    }
}
