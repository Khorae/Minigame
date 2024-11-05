package net.minecraft.network.protocol.common.custom;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.resources.ResourceLocation;

public record BreezeDebugPayload(BreezeDebugPayload.BreezeInfo breezeInfo) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("debug/breeze");

    public BreezeDebugPayload(FriendlyByteBuf buf) {
        this(new BreezeDebugPayload.BreezeInfo(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.breezeInfo.write(buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static record BreezeInfo(UUID uuid, int id, Integer attackTarget, BlockPos jumpTarget) {
        public BreezeInfo(FriendlyByteBuf buf) {
            this(buf.readUUID(), buf.readInt(), buf.readNullable(FriendlyByteBuf::readInt), buf.readNullable(FriendlyByteBuf::readBlockPos));
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeUUID(this.uuid);
            buf.writeInt(this.id);
            buf.writeNullable(this.attackTarget, FriendlyByteBuf::writeInt);
            buf.writeNullable(this.jumpTarget, FriendlyByteBuf::writeBlockPos);
        }

        public String generateName() {
            return DebugEntityNameGenerator.getEntityName(this.uuid);
        }

        @Override
        public String toString() {
            return this.generateName();
        }
    }
}
