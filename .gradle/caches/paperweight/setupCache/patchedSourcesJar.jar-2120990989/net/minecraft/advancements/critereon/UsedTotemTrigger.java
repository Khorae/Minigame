package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class UsedTotemTrigger extends SimpleCriterionTrigger<UsedTotemTrigger.TriggerInstance> {
    @Override
    public Codec<UsedTotemTrigger.TriggerInstance> codec() {
        return UsedTotemTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack) {
        this.trigger(player, conditions -> conditions.matches(stack));
    }

    public static record TriggerInstance(@Override Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<UsedTotemTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                        ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(UsedTotemTrigger.TriggerInstance::player),
                        ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(UsedTotemTrigger.TriggerInstance::item)
                    )
                    .apply(instance, UsedTotemTrigger.TriggerInstance::new)
        );

        public static Criterion<UsedTotemTrigger.TriggerInstance> usedTotem(ItemPredicate itemPredicate) {
            return CriteriaTriggers.USED_TOTEM.createCriterion(new UsedTotemTrigger.TriggerInstance(Optional.empty(), Optional.of(itemPredicate)));
        }

        public static Criterion<UsedTotemTrigger.TriggerInstance> usedTotem(ItemLike item) {
            return CriteriaTriggers.USED_TOTEM
                .createCriterion(new UsedTotemTrigger.TriggerInstance(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(item).build())));
        }

        public boolean matches(ItemStack stack) {
            return this.item.isEmpty() || this.item.get().matches(stack);
        }
    }
}
