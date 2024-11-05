package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record BrandPayload(String brand) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("brand");

    public BrandPayload(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.brand);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
