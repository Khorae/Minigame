package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public record BrainDebugPayload(BrainDebugPayload.BrainDump brainDump) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("debug/brain");

    public BrainDebugPayload(FriendlyByteBuf buf) {
        this(new BrainDebugPayload.BrainDump(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.brainDump.write(buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static record BrainDump(
        UUID uuid,
        int id,
        String name,
        String profession,
        int xp,
        float health,
        float maxHealth,
        Vec3 pos,
        String inventory,
        @Nullable Path path,
        boolean wantsGolem,
        int angerLevel,
        List<String> activities,
        List<String> behaviors,
        List<String> memories,
        List<String> gossips,
        Set<BlockPos> pois,
        Set<BlockPos> potentialPois
    ) {
        public BrainDump(FriendlyByteBuf buf) {
            this(
                buf.readUUID(),
                buf.readInt(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readVec3(),
                buf.readUtf(),
                buf.readNullable(Path::createFromStream),
                buf.readBoolean(),
                buf.readInt(),
                buf.readList(FriendlyByteBuf::readUtf),
                buf.readList(FriendlyByteBuf::readUtf),
                buf.readList(FriendlyByteBuf::readUtf),
                buf.readList(FriendlyByteBuf::readUtf),
                buf.readCollection(HashSet::new, FriendlyByteBuf::readBlockPos),
                buf.readCollection(HashSet::new, FriendlyByteBuf::readBlockPos)
            );
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeUUID(this.uuid);
            buf.writeInt(this.id);
            buf.writeUtf(this.name);
            buf.writeUtf(this.profession);
            buf.writeInt(this.xp);
            buf.writeFloat(this.health);
            buf.writeFloat(this.maxHealth);
            buf.writeVec3(this.pos);
            buf.writeUtf(this.inventory);
            buf.writeNullable(this.path, (bufx, path) -> path.writeToStream(bufx));
            buf.writeBoolean(this.wantsGolem);
            buf.writeInt(this.angerLevel);
            buf.writeCollection(this.activities, FriendlyByteBuf::writeUtf);
            buf.writeCollection(this.behaviors, FriendlyByteBuf::writeUtf);
            buf.writeCollection(this.memories, FriendlyByteBuf::writeUtf);
            buf.writeCollection(this.gossips, FriendlyByteBuf::writeUtf);
            buf.writeCollection(this.pois, FriendlyByteBuf::writeBlockPos);
            buf.writeCollection(this.potentialPois, FriendlyByteBuf::writeBlockPos);
        }

        public boolean hasPoi(BlockPos pos) {
            return this.pois.contains(pos);
        }

        public boolean hasPotentialPoi(BlockPos pos) {
            return this.potentialPois.contains(pos);
        }
    }
}
