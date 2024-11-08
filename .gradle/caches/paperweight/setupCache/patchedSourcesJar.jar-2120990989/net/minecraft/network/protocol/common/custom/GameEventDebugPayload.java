package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record GameEventDebugPayload(ResourceKey<GameEvent> type, Vec3 pos) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("debug/game_event");

    public GameEventDebugPayload(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.GAME_EVENT), buf.readVec3());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(this.type);
        buf.writeVec3(this.pos);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
