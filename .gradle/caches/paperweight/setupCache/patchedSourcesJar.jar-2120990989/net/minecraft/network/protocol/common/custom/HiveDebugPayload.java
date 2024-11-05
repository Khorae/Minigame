package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record HiveDebugPayload(HiveDebugPayload.HiveInfo hiveInfo) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("debug/hive");

    public HiveDebugPayload(FriendlyByteBuf buf) {
        this(new HiveDebugPayload.HiveInfo(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.hiveInfo.write(buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static record HiveInfo(BlockPos pos, String hiveType, int occupantCount, int honeyLevel, boolean sedated) {
        public HiveInfo(FriendlyByteBuf buf) {
            this(buf.readBlockPos(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readBoolean());
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeBlockPos(this.pos);
            buf.writeUtf(this.hiveType);
            buf.writeInt(this.occupantCount);
            buf.writeInt(this.honeyLevel);
            buf.writeBoolean(this.sedated);
        }
    }
}
