package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EnterBlockTrigger extends SimpleCriterionTrigger<EnterBlockTrigger.TriggerInstance> {
    @Override
    public Codec<EnterBlockTrigger.TriggerInstance> codec() {
        return EnterBlockTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, BlockState state) {
        this.trigger(player, conditions -> conditions.matches(state));
    }

    public static record TriggerInstance(
        @Override Optional<ContextAwarePredicate> player, Optional<Holder<Block>> block, Optional<StatePropertiesPredicate> state
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<EnterBlockTrigger.TriggerInstance> CODEC = ExtraCodecs.validate(
            RecordCodecBuilder.create(
                instance -> instance.group(
                            ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(EnterBlockTrigger.TriggerInstance::player),
                            ExtraCodecs.strictOptionalField(BuiltInRegistries.BLOCK.holderByNameCodec(), "block")
                                .forGetter(EnterBlockTrigger.TriggerInstance::block),
                            ExtraCodecs.strictOptionalField(StatePropertiesPredicate.CODEC, "state").forGetter(EnterBlockTrigger.TriggerInstance::state)
                        )
                        .apply(instance, EnterBlockTrigger.TriggerInstance::new)
            ),
            EnterBlockTrigger.TriggerInstance::validate
        );

        private static DataResult<EnterBlockTrigger.TriggerInstance> validate(EnterBlockTrigger.TriggerInstance conditions) {
            return conditions.block
                .<DataResult<EnterBlockTrigger.TriggerInstance>>flatMap(
                    block -> conditions.state
                            .<String>flatMap(state -> state.checkState(((Block)block.value()).getStateDefinition()))
                            .map(property -> DataResult.error(() -> "Block" + block + " has no property " + property))
                )
                .orElseGet(() -> DataResult.success(conditions));
        }

        public static Criterion<EnterBlockTrigger.TriggerInstance> entersBlock(Block block) {
            return CriteriaTriggers.ENTER_BLOCK
                .createCriterion(new EnterBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(block.builtInRegistryHolder()), Optional.empty()));
        }

        public boolean matches(BlockState state) {
            return (!this.block.isPresent() || state.is(this.block.get())) && (!this.state.isPresent() || this.state.get().matches(state));
        }
    }
}