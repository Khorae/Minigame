package net.minecraft.advancements.critereon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public record LightningBoltPredicate(MinMaxBounds.Ints blocksSetOnFire, Optional<EntityPredicate> entityStruck) implements EntitySubPredicate {
    public static final MapCodec<LightningBoltPredicate> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                    ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "blocks_set_on_fire", MinMaxBounds.Ints.ANY)
                        .forGetter(LightningBoltPredicate::blocksSetOnFire),
                    ExtraCodecs.strictOptionalField(EntityPredicate.CODEC, "entity_struck").forGetter(LightningBoltPredicate::entityStruck)
                )
                .apply(instance, LightningBoltPredicate::new)
    );

    public static LightningBoltPredicate blockSetOnFire(MinMaxBounds.Ints blocksSetOnFire) {
        return new LightningBoltPredicate(blocksSetOnFire, Optional.empty());
    }

    @Override
    public EntitySubPredicate.Type type() {
        return EntitySubPredicate.Types.LIGHTNING;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel world, @Nullable Vec3 pos) {
        return entity instanceof LightningBolt lightningBolt
            && this.blocksSetOnFire.matches(lightningBolt.getBlocksSetOnFire())
            && (
                this.entityStruck.isEmpty()
                    || lightningBolt.getHitEntities().anyMatch(struckEntity -> this.entityStruck.get().matches(world, pos, struckEntity))
            );
    }
}