package net.minecraft.network.protocol.common;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagNetworkSerialization;

public class ClientboundUpdateTagsPacket implements Packet<ClientCommonPacketListener> {
    private final Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> tags;

    public ClientboundUpdateTagsPacket(Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> groups) {
        this.tags = groups;
    }

    public ClientboundUpdateTagsPacket(FriendlyByteBuf buf) {
        this.tags = buf.readMap(FriendlyByteBuf::readRegistryKey, TagNetworkSerialization.NetworkPayload::read);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(this.tags, FriendlyByteBuf::writeResourceKey, (bufx, serializedGroup) -> serializedGroup.write(bufx));
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleUpdateTags(this);
    }

    public Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> getTags() {
        return this.tags;
    }
}
