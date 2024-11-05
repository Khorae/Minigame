package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;

public class ServerboundSetBeaconPacket implements Packet<ServerGamePacketListener> {
    private final Optional<MobEffect> primary;
    private final Optional<MobEffect> secondary;

    public ServerboundSetBeaconPacket(Optional<MobEffect> primaryEffectId, Optional<MobEffect> secondaryEffectId) {
        this.primary = primaryEffectId;
        this.secondary = secondaryEffectId;
    }

    public ServerboundSetBeaconPacket(FriendlyByteBuf buf) {
        this.primary = buf.readOptional(buf2 -> buf2.readById(BuiltInRegistries.MOB_EFFECT));
        this.secondary = buf.readOptional(buf2 -> buf2.readById(BuiltInRegistries.MOB_EFFECT));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeOptional(this.primary, (buf2, primaryEffectId) -> buf2.writeId(BuiltInRegistries.MOB_EFFECT, primaryEffectId));
        buf.writeOptional(this.secondary, (buf2, secondaryEffectId) -> buf2.writeId(BuiltInRegistries.MOB_EFFECT, secondaryEffectId));
    }

    @Override
    public void handle(ServerGamePacketListener listener) {
        listener.handleSetBeaconPacket(this);
    }

    public Optional<MobEffect> getPrimary() {
        return this.primary;
    }

    public Optional<MobEffect> getSecondary() {
        return this.secondary;
    }
}
