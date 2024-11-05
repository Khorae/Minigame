package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record NeighborUpdatesDebugPayload(long time, BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("debug/neighbors_update");

    public NeighborUpdatesDebugPayload(FriendlyByteBuf buf) {
        this(buf.readVarLong(), buf.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarLong(this.time);
        buf.writeBlockPos(this.pos);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
