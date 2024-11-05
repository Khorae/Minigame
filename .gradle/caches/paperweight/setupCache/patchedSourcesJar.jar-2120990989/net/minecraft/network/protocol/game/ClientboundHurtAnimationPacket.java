package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.LivingEntity;

public record ClientboundHurtAnimationPacket(int id, float yaw) implements Packet<ClientGamePacketListener> {
    public ClientboundHurtAnimationPacket(LivingEntity entity) {
        this(entity.getId(), entity.getHurtDir());
    }

    public ClientboundHurtAnimationPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readFloat());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeFloat(this.yaw);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleHurtAnimation(this);
    }
}
