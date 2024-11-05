package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerCombatKillPacket implements Packet<ClientGamePacketListener> {
    private final int playerId;
    private final Component message;

    public ClientboundPlayerCombatKillPacket(int entityId, Component message) {
        this.playerId = entityId;
        this.message = message;
    }

    public ClientboundPlayerCombatKillPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readVarInt();
        this.message = buf.readComponentTrusted();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.playerId);
        buf.writeComponent(this.message);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handlePlayerCombatKill(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public Component getMessage() {
        return this.message;
    }
}
