package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundResetScorePacket(String owner, @Nullable String objectiveName) implements Packet<ClientGamePacketListener> {
    public ClientboundResetScorePacket(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readNullable(FriendlyByteBuf::readUtf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.owner);
        buf.writeNullable(this.objectiveName, FriendlyByteBuf::writeUtf);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleResetScore(this);
    }
}
