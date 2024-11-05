package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;

public record ClientboundForgetLevelChunkPacket(ChunkPos pos) implements Packet<ClientGamePacketListener> {
    public ClientboundForgetLevelChunkPacket(FriendlyByteBuf buf) {
        this(buf.readChunkPos());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeChunkPos(this.pos);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleForgetLevelChunk(this);
    }
}
