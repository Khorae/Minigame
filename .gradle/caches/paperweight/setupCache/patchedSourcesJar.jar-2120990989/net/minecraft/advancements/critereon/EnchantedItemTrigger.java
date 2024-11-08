package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public class EnchantedItemTrigger extends SimpleCriterionTrigger<EnchantedItemTrigger.TriggerInstance> {
    @Override
    public Codec<EnchantedItemTrigger.TriggerInstance> codec() {
        return EnchantedItemTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack, int levels) {
        this.trigger(player, conditions -> conditions.matches(stack, levels));
    }

    public static record TriggerInstance(@Override Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, MinMaxBounds.Ints levels)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<EnchantedItemTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                        ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(EnchantedItemTrigger.TriggerInstance::player),
                        ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(EnchantedItemTrigger.TriggerInstance::item),
                        ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "levels", MinMaxBounds.Ints.ANY)
                            .forGetter(EnchantedItemTrigger.TriggerInstance::levels)
                    )
                    .apply(instance, EnchantedItemTrigger.TriggerInstance::new)
        );

        public static Criterion<EnchantedItemTrigger.TriggerInstance> enchantedItem() {
            return CriteriaTriggers.ENCHANTED_ITEM
                .createCriterion(new EnchantedItemTrigger.TriggerInstance(Optional.empty(), Optional.empty(), MinMaxBounds.Ints.ANY));
        }

        public boolean matches(ItemStack stack, int levels) {
            return (!this.item.isPresent() || this.item.get().matches(stack)) && this.levels.matches(levels);
        }
    }
}
