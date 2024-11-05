package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundOpenSignEditorPacket implements Packet<ClientGamePacketListener> {
    private final BlockPos pos;
    private final boolean isFrontText;

    public ClientboundOpenSignEditorPacket(BlockPos pos, boolean front) {
        this.pos = pos;
        this.isFrontText = front;
    }

    public ClientboundOpenSignEditorPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.isFrontText = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.isFrontText);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleOpenSignEditor(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean isFrontText() {
        return this.isFrontText;
    }
}
