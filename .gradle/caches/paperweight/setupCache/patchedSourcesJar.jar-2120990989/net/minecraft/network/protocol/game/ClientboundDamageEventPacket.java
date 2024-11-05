package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record ClientboundDamageEventPacket(int entityId, int sourceTypeId, int sourceCauseId, int sourceDirectId, Optional<Vec3> sourcePosition)
    implements Packet<ClientGamePacketListener> {
    public ClientboundDamageEventPacket(Entity entity, DamageSource damageSource) {
        this(
            entity.getId(),
            entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getId(damageSource.type()),
            damageSource.getEntity() != null ? damageSource.getEntity().getId() : -1,
            damageSource.getDirectEntity() != null ? damageSource.getDirectEntity().getId() : -1,
            Optional.ofNullable(damageSource.sourcePositionRaw())
        );
    }

    public ClientboundDamageEventPacket(FriendlyByteBuf buf) {
        this(
            buf.readVarInt(),
            buf.readVarInt(),
            readOptionalEntityId(buf),
            readOptionalEntityId(buf),
            buf.readOptional(pos -> new Vec3(pos.readDouble(), pos.readDouble(), pos.readDouble()))
        );
    }

    private static void writeOptionalEntityId(FriendlyByteBuf buf, int value) {
        buf.writeVarInt(value + 1);
    }

    private static int readOptionalEntityId(FriendlyByteBuf buf) {
        return buf.readVarInt() - 1;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.sourceTypeId);
        writeOptionalEntityId(buf, this.sourceCauseId);
        writeOptionalEntityId(buf, this.sourceDirectId);
        buf.writeOptional(this.sourcePosition, (bufx, pos) -> {
            bufx.writeDouble(pos.x());
            bufx.writeDouble(pos.y());
            bufx.writeDouble(pos.z());
        });
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleDamageEvent(this);
    }

    public DamageSource getSource(Level world) {
        Holder<DamageType> holder = world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(this.sourceTypeId).get();
        if (this.sourcePosition.isPresent()) {
            return new DamageSource(holder, this.sourcePosition.get());
        } else {
            Entity entity = world.getEntity(this.sourceCauseId);
            Entity entity2 = world.getEntity(this.sourceDirectId);
            return new DamageSource(holder, entity2, entity);
        }
    }
}