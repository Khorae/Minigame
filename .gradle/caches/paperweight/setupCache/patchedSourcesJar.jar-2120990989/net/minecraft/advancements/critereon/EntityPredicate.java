package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

public record EntityPredicate(
    Optional<EntityTypePredicate> entityType,
    Optional<DistancePredicate> distanceToPlayer,
    Optional<LocationPredicate> location,
    Optional<LocationPredicate> steppingOnLocation,
    Optional<MobEffectsPredicate> effects,
    Optional<NbtPredicate> nbt,
    Optional<EntityFlagsPredicate> flags,
    Optional<EntityEquipmentPredicate> equipment,
    Optional<EntitySubPredicate> subPredicate,
    Optional<EntityPredicate> vehicle,
    Optional<EntityPredicate> passenger,
    Optional<EntityPredicate> targetedEntity,
    Optional<String> team
) {
    public static final Codec<EntityPredicate> CODEC = ExtraCodecs.recursive(
        "EntityPredicate",
        entityPredicateCodec -> RecordCodecBuilder.create(
                instance -> instance.group(
                            ExtraCodecs.strictOptionalField(EntityTypePredicate.CODEC, "type").forGetter(EntityPredicate::entityType),
                            ExtraCodecs.strictOptionalField(DistancePredicate.CODEC, "distance").forGetter(EntityPredicate::distanceToPlayer),
                            ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "location").forGetter(EntityPredicate::location),
                            ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "stepping_on").forGetter(EntityPredicate::steppingOnLocation),
                            ExtraCodecs.strictOptionalField(MobEffectsPredicate.CODEC, "effects").forGetter(EntityPredicate::effects),
                            ExtraCodecs.strictOptionalField(NbtPredicate.CODEC, "nbt").forGetter(EntityPredicate::nbt),
                            ExtraCodecs.strictOptionalField(EntityFlagsPredicate.CODEC, "flags").forGetter(EntityPredicate::flags),
                            ExtraCodecs.strictOptionalField(EntityEquipmentPredicate.CODEC, "equipment").forGetter(EntityPredicate::equipment),
                            ExtraCodecs.strictOptionalField(EntitySubPredicate.CODEC, "type_specific").forGetter(EntityPredicate::subPredicate),
                            ExtraCodecs.strictOptionalField(entityPredicateCodec, "vehicle").forGetter(EntityPredicate::vehicle),
                            ExtraCodecs.strictOptionalField(entityPredicateCodec, "passenger").forGetter(EntityPredicate::passenger),
                            ExtraCodecs.strictOptionalField(entityPredicateCodec, "targeted_entity").forGetter(EntityPredicate::targetedEntity),
                            ExtraCodecs.strictOptionalField(Codec.STRING, "team").forGetter(EntityPredicate::team)
                        )
                        .apply(instance, EntityPredicate::new)
            )
    );
    public static final Codec<ContextAwarePredicate> ADVANCEMENT_CODEC = ExtraCodecs.withAlternative(ContextAwarePredicate.CODEC, CODEC, EntityPredicate::wrap);

    public static ContextAwarePredicate wrap(EntityPredicate.Builder builder) {
        return wrap(builder.build());
    }

    public static Optional<ContextAwarePredicate> wrap(Optional<EntityPredicate> entityPredicate) {
        return entityPredicate.map(EntityPredicate::wrap);
    }

    public static List<ContextAwarePredicate> wrap(EntityPredicate.Builder... builders) {
        return Stream.of(builders).map(EntityPredicate::wrap).toList();
    }

    public static ContextAwarePredicate wrap(EntityPredicate predicate) {
        LootItemCondition lootItemCondition = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, predicate).build();
        return new ContextAwarePredicate(List.of(lootItemCondition));
    }

    public boolean matches(ServerPlayer player, @Nullable Entity entity) {
        return this.matches(player.serverLevel(), player.position(), entity);
    }

    public boolean matches(ServerLevel world, @Nullable Vec3 pos, @Nullable Entity entity) {
        if (entity == null) {
            return false;
        } else if (this.entityType.isPresent() && !this.entityType.get().matches(entity.getType())) {
            return false;
        } else {
            if (pos == null) {
                if (this.distanceToPlayer.isPresent()) {
                    return false;
                }
            } else if (this.distanceToPlayer.isPresent()
                && !this.distanceToPlayer.get().matches(pos.x, pos.y, pos.z, entity.getX(), entity.getY(), entity.getZ())) {
                return false;
            }

            if (this.location.isPresent() && !this.location.get().matches(world, entity.getX(), entity.getY(), entity.getZ())) {
                return false;
            } else {
                if (this.steppingOnLocation.isPresent()) {
                    Vec3 vec3 = Vec3.atCenterOf(entity.getOnPos());
                    if (!this.steppingOnLocation.get().matches(world, vec3.x(), vec3.y(), vec3.z())) {
                        return false;
                    }
                }

                if (this.effects.isPresent() && !this.effects.get().matches(entity)) {
                    return false;
                } else if (this.nbt.isPresent() && !this.nbt.get().matches(entity)) {
                    return false;
                } else if (this.flags.isPresent() && !this.flags.get().matches(entity)) {
                    return false;
                } else if (this.equipment.isPresent() && !this.equipment.get().matches(entity)) {
                    return false;
                } else if (this.subPredicate.isPresent() && !this.subPredicate.get().matches(entity, world, pos)) {
                    return false;
                } else if (this.vehicle.isPresent() && !this.vehicle.get().matches(world, pos, entity.getVehicle())) {
                    return false;
                } else if (this.passenger.isPresent()
                    && entity.getPassengers().stream().noneMatch(entityx -> this.passenger.get().matches(world, pos, entityx))) {
                    return false;
                } else if (this.targetedEntity.isPresent()
                    && !this.targetedEntity.get().matches(world, pos, entity instanceof Mob ? ((Mob)entity).getTarget() : null)) {
                    return false;
                } else {
                    if (this.team.isPresent()) {
                        Team team = entity.getTeam();
                        if (team == null || !this.team.get().equals(team.getName())) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
    }

    public static LootContext createContext(ServerPlayer player, Entity target) {
        LootParams lootParams = new LootParams.Builder(player.serverLevel())
            .withParameter(LootContextParams.THIS_ENTITY, target)
            .withParameter(LootContextParams.ORIGIN, player.position())
            .create(LootContextParamSets.ADVANCEMENT_ENTITY);
        return new LootContext.Builder(lootParams).create(Optional.empty());
    }

    public static class Builder {
        private Optional<EntityTypePredicate> entityType = Optional.empty();
        private Optional<DistancePredicate> distanceToPlayer = Optional.empty();
        private Optional<LocationPredicate> location = Optional.empty();
        private Optional<LocationPredicate> steppingOnLocation = Optional.empty();
        private Optional<MobEffectsPredicate> effects = Optional.empty();
        private Optional<NbtPredicate> nbt = Optional.empty();
        private Optional<EntityFlagsPredicate> flags = Optional.empty();
        private Optional<EntityEquipmentPredicate> equipment = Optional.empty();
        private Optional<EntitySubPredicate> subPredicate = Optional.empty();
        private Optional<EntityPredicate> vehicle = Optional.empty();
        private Optional<EntityPredicate> passenger = Optional.empty();
        private Optional<EntityPredicate> targetedEntity = Optional.empty();
        private Optional<String> team = Optional.empty();

        public static EntityPredicate.Builder entity() {
            return new EntityPredicate.Builder();
        }

        public EntityPredicate.Builder of(EntityType<?> type) {
            this.entityType = Optional.of(EntityTypePredicate.of(type));
            return this;
        }

        public EntityPredicate.Builder of(TagKey<EntityType<?>> tag) {
            this.entityType = Optional.of(EntityTypePredicate.of(tag));
            return this;
        }

        public EntityPredicate.Builder entityType(EntityTypePredicate type) {
            this.entityType = Optional.of(type);
            return this;
        }

        public EntityPredicate.Builder distance(DistancePredicate distance) {
            this.distanceToPlayer = Optional.of(distance);
            return this;
        }

        public EntityPredicate.Builder located(LocationPredicate.Builder location) {
            this.location = Optional.of(location.build());
            return this;
        }

        public EntityPredicate.Builder steppingOn(LocationPredicate.Builder steppingOn) {
            this.steppingOnLocation = Optional.of(steppingOn.build());
            return this;
        }

        public EntityPredicate.Builder effects(MobEffectsPredicate.Builder effects) {
            this.effects = effects.build();
            return this;
        }

        public EntityPredicate.Builder nbt(NbtPredicate nbt) {
            this.nbt = Optional.of(nbt);
            return this;
        }

        public EntityPredicate.Builder flags(EntityFlagsPredicate.Builder flags) {
            this.flags = Optional.of(flags.build());
            return this;
        }

        public EntityPredicate.Builder equipment(EntityEquipmentPredicate.Builder equipment) {
            this.equipment = Optional.of(equipment.build());
            return this;
        }

        public EntityPredicate.Builder equipment(EntityEquipmentPredicate equipment) {
            this.equipment = Optional.of(equipment);
            return this;
        }

        public EntityPredicate.Builder subPredicate(EntitySubPredicate typeSpecific) {
            this.subPredicate = Optional.of(typeSpecific);
            return this;
        }

        public EntityPredicate.Builder vehicle(EntityPredicate.Builder vehicle) {
            this.vehicle = Optional.of(vehicle.build());
            return this;
        }

        public EntityPredicate.Builder passenger(EntityPredicate.Builder passenger) {
            this.passenger = Optional.of(passenger.build());
            return this;
        }

        public EntityPredicate.Builder targetedEntity(EntityPredicate.Builder targetedEntity) {
            this.targetedEntity = Optional.of(targetedEntity.build());
            return this;
        }

        public EntityPredicate.Builder team(String team) {
            this.team = Optional.of(team);
            return this;
        }

        public EntityPredicate build() {
            return new EntityPredicate(
                this.entityType,
                this.distanceToPlayer,
                this.location,
                this.steppingOnLocation,
                this.effects,
                this.nbt,
                this.flags,
                this.equipment,
                this.subPredicate,
                this.vehicle,
                this.passenger,
                this.targetedEntity,
                this.team
            );
        }
    }
}
