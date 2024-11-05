package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatEndPacket implements Packet<ClientGamePacketListener> {
    private final int duration;

    public ClientboundPlayerCombatEndPacket(CombatTracker damageTracker) {
        this(damageTracker.getCombatDuration());
    }

    public ClientboundPlayerCombatEndPacket(int timeSinceLastAttack) {
        this.duration = timeSinceLastAttack;
    }

    public ClientboundPlayerCombatEndPacket(FriendlyByteBuf buf) {
        this.duration = buf.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.duration);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handlePlayerCombatEnd(this);
    }
}
