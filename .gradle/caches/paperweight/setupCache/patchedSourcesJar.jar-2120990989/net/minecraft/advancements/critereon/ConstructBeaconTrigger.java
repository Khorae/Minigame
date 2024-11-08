package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

public class ConstructBeaconTrigger extends SimpleCriterionTrigger<ConstructBeaconTrigger.TriggerInstance> {
    @Override
    public Codec<ConstructBeaconTrigger.TriggerInstance> codec() {
        return ConstructBeaconTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, int level) {
        this.trigger(player, conditions -> conditions.matches(level));
    }

    public static record TriggerInstance(@Override Optional<ContextAwarePredicate> player, MinMaxBounds.Ints level)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<ConstructBeaconTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                        ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(ConstructBeaconTrigger.TriggerInstance::player),
                        ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "level", MinMaxBounds.Ints.ANY)
                            .forGetter(ConstructBeaconTrigger.TriggerInstance::level)
                    )
                    .apply(instance, ConstructBeaconTrigger.TriggerInstance::new)
        );

        public static Criterion<ConstructBeaconTrigger.TriggerInstance> constructedBeacon() {
            return CriteriaTriggers.CONSTRUCT_BEACON.createCriterion(new ConstructBeaconTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.ANY));
        }

        public static Criterion<ConstructBeaconTrigger.TriggerInstance> constructedBeacon(MinMaxBounds.Ints level) {
            return CriteriaTriggers.CONSTRUCT_BEACON.createCriterion(new ConstructBeaconTrigger.TriggerInstance(Optional.empty(), level));
        }

        public boolean matches(int level) {
            return this.level.matches(level);
        }
    }
}
