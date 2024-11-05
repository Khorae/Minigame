package net.minecraft.network.protocol.common;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundResourcePackPacket(UUID id, ServerboundResourcePackPacket.Action action) implements Packet<ServerCommonPacketListener> {
    public ServerboundResourcePackPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readEnum(ServerboundResourcePackPacket.Action.class));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.id);
        buf.writeEnum(this.action);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleResourcePackResponse(this);
    }

    public static enum Action {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED,
        DOWNLOADED,
        INVALID_URL,
        FAILED_RELOAD,
        DISCARDED;

        public boolean isTerminal() {
            return this != ACCEPTED && this != DOWNLOADED;
        }
    }
}
