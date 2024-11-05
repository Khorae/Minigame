package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record VillageSectionsDebugPayload(Set<SectionPos> villageChunks, Set<SectionPos> notVillageChunks) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("debug/village_sections");

    public VillageSectionsDebugPayload(FriendlyByteBuf buf) {
        this(buf.readCollection(HashSet::new, FriendlyByteBuf::readSectionPos), buf.readCollection(HashSet::new, FriendlyByteBuf::readSectionPos));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(this.villageChunks, FriendlyByteBuf::writeSectionPos);
        buf.writeCollection(this.notVillageChunks, FriendlyByteBuf::writeSectionPos);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
