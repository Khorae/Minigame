package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.protocol.Packet;

public record ClientboundSetScorePacket(String owner, String objectiveName, int score, @Nullable Component display, @Nullable NumberFormat numberFormat)
    implements Packet<ClientGamePacketListener> {
    public ClientboundSetScorePacket(FriendlyByteBuf buf) {
        this(
            buf.readUtf(),
            buf.readUtf(),
            buf.readVarInt(),
            buf.readNullable(FriendlyByteBuf::readComponentTrusted),
            buf.readNullable(NumberFormatTypes::readFromStream)
        );
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.owner);
        buf.writeUtf(this.objectiveName);
        buf.writeVarInt(this.score);
        buf.writeNullable(this.display, FriendlyByteBuf::writeComponent);
        buf.writeNullable(this.numberFormat, NumberFormatTypes::writeToStream);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleSetScore(this);
    }
}
