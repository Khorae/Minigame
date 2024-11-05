package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record GameTestAddMarkerDebugPayload(BlockPos pos, int color, String text, int durationMs) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("debug/game_test_add_marker");

    public GameTestAddMarkerDebugPayload(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt(), buf.readUtf(), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.color);
        buf.writeUtf(this.text);
        buf.writeInt(this.durationMs);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
