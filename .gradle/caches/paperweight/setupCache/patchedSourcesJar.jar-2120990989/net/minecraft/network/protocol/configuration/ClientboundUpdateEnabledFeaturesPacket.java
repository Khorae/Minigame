package net.minecraft.network.protocol.configuration;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateEnabledFeaturesPacket(Set<ResourceLocation> features) implements Packet<ClientConfigurationPacketListener> {
    public ClientboundUpdateEnabledFeaturesPacket(FriendlyByteBuf buf) {
        this(buf.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(this.features, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public void handle(ClientConfigurationPacketListener listener) {
        listener.handleEnabledFeatures(this);
    }
}
