package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3;

public record DamageSourcePredicate(List<TagPredicate<DamageType>> tags, Optional<EntityPredicate> directEntity, Optional<EntityPredicate> sourceEntity) {
    public static final Codec<DamageSourcePredicate> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
                    ExtraCodecs.strictOptionalField(TagPredicate.codec(Registries.DAMAGE_TYPE).listOf(), "tags", List.of())
                        .forGetter(DamageSourcePredicate::tags),
                    ExtraCodecs.strictOptionalField(EntityPredicate.CODEC, "direct_entity").forGetter(DamageSourcePredicate::directEntity),
                    ExtraCodecs.strictOptionalField(EntityPredicate.CODEC, "source_entity").forGetter(DamageSourcePredicate::sourceEntity)
                )
                .apply(instance, DamageSourcePredicate::new)
    );

    public boolean matches(ServerPlayer player, DamageSource damageSource) {
        return this.matches(player.serverLevel(), player.position(), damageSource);
    }

    public boolean matches(ServerLevel world, Vec3 pos, DamageSource damageSource) {
        for (TagPredicate<DamageType> tagPredicate : this.tags) {
            if (!tagPredicate.matches(damageSource.typeHolder())) {
                return false;
            }
        }

        return (!this.directEntity.isPresent() || this.directEntity.get().matches(world, pos, damageSource.getDirectEntity()))
            && (!this.sourceEntity.isPresent() || this.sourceEntity.get().matches(world, pos, damageSource.getEntity()));
    }

    public static class Builder {
        private final ImmutableList.Builder<TagPredicate<DamageType>> tags = ImmutableList.builder();
        private Optional<EntityPredicate> directEntity = Optional.empty();
        private Optional<EntityPredicate> sourceEntity = Optional.empty();

        public static DamageSourcePredicate.Builder damageType() {
            return new DamageSourcePredicate.Builder();
        }

        public DamageSourcePredicate.Builder tag(TagPredicate<DamageType> tagPredicate) {
            this.tags.add(tagPredicate);
            return this;
        }

        public DamageSourcePredicate.Builder direct(EntityPredicate.Builder entity) {
            this.directEntity = Optional.of(entity.build());
            return this;
        }

        public DamageSourcePredicate.Builder source(EntityPredicate.Builder entity) {
            this.sourceEntity = Optional.of(entity.build());
            return this;
        }

        public DamageSourcePredicate build() {
            return new DamageSourcePredicate(this.tags.build(), this.directEntity, this.sourceEntity);
        }
    }
}
