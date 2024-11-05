package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record DiscardedPayload(@Override ResourceLocation id) implements CustomPacketPayload {
    @Override
    public void write(FriendlyByteBuf buf) {
    }
}