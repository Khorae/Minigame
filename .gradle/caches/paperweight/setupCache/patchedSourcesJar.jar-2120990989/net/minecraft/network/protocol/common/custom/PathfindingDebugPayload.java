package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.pathfinder.Path;

public record PathfindingDebugPayload(int entityId, Path path, float maxNodeDistance) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("debug/path");

    public PathfindingDebugPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), Path.createFromStream(buf), buf.readFloat());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        this.path.writeToStream(buf);
        buf.writeFloat(this.maxNodeDistance);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
