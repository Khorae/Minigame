package net.minecraft.network.protocol.common;

import net.minecraft.network.ClientboundPacketListener;

public interface ClientCommonPacketListener extends ClientboundPacketListener {
    void handleKeepAlive(ClientboundKeepAlivePacket packet);

    void handlePing(ClientboundPingPacket packet);

    void handleCustomPayload(ClientboundCustomPayloadPacket packet);

    void handleDisconnect(ClientboundDisconnectPacket packet);

    void handleResourcePackPush(ClientboundResourcePackPushPacket packet);

    void handleResourcePackPop(ClientboundResourcePackPopPacket packet);

    void handleUpdateTags(ClientboundUpdateTagsPacket packet);
}
