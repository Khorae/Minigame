package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;

public record ItemPredicate(
    Optional<TagKey<Item>> tag,
    Optional<HolderSet<Item>> items,
    MinMaxBounds.Ints count,
    MinMaxBounds.Ints durability,
    List<EnchantmentPredicate> enchantments,
    List<EnchantmentPredicate> storedEnchantments,
    Optional<Holder<Potion>> potion,
    Optional<NbtPredicate> nbt
) {
    private static final Codec<HolderSet<Item>> ITEMS_CODEC = BuiltInRegistries.ITEM
        .holderByNameCodec()
        .listOf()
        .xmap(HolderSet::direct, items -> items.stream().toList());
    public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
                    ExtraCodecs.strictOptionalField(TagKey.codec(Registries.ITEM), "tag").forGetter(ItemPredicate::tag),
                    ExtraCodecs.strictOptionalField(ITEMS_CODEC, "items").forGetter(ItemPredicate::items),
                    ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "count", MinMaxBounds.Ints.ANY).forGetter(ItemPredicate::count),
                    ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "durability", MinMaxBounds.Ints.ANY).forGetter(ItemPredicate::durability),
                    ExtraCodecs.strictOptionalField(EnchantmentPredicate.CODEC.listOf(), "enchantments", List.of()).forGetter(ItemPredicate::enchantments),
                    ExtraCodecs.strictOptionalField(EnchantmentPredicate.CODEC.listOf(), "stored_enchantments", List.of())
                        .forGetter(ItemPredicate::storedEnchantments),
                    ExtraCodecs.strictOptionalField(BuiltInRegistries.POTION.holderByNameCodec(), "potion").forGetter(ItemPredicate::potion),
                    ExtraCodecs.strictOptionalField(NbtPredicate.CODEC, "nbt").forGetter(ItemPredicate::nbt)
                )
                .apply(instance, ItemPredicate::new)
    );

    public boolean matches(ItemStack stack) {
        if (this.tag.isPresent() && !stack.is(this.tag.get())) {
            return false;
        } else if (this.items.isPresent() && !stack.is(this.items.get())) {
            return false;
        } else if (!this.count.matches(stack.getCount())) {
            return false;
        } else if (!this.durability.isAny() && !stack.isDamageableItem()) {
            return false;
        } else if (!this.durability.matches(stack.getMaxDamage() - stack.getDamageValue())) {
            return false;
        } else if (this.nbt.isPresent() && !this.nbt.get().matches(stack)) {
            return false;
        } else {
            if (!this.enchantments.isEmpty()) {
                Map<Enchantment, Integer> map = EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags());

                for (EnchantmentPredicate enchantmentPredicate : this.enchantments) {
                    if (!enchantmentPredicate.containedIn(map)) {
                        return false;
                    }
                }
            }

            if (!this.storedEnchantments.isEmpty()) {
                Map<Enchantment, Integer> map2 = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(stack));

                for (EnchantmentPredicate enchantmentPredicate2 : this.storedEnchantments) {
                    if (!enchantmentPredicate2.containedIn(map2)) {
                        return false;
                    }
                }
            }

            return !this.potion.isPresent() || this.potion.get().value() == PotionUtils.getPotion(stack);
        }
    }

    public static class Builder {
        private final ImmutableList.Builder<EnchantmentPredicate> enchantments = ImmutableList.builder();
        private final ImmutableList.Builder<EnchantmentPredicate> storedEnchantments = ImmutableList.builder();
        private Optional<HolderSet<Item>> items = Optional.empty();
        private Optional<TagKey<Item>> tag = Optional.empty();
        private MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
        private MinMaxBounds.Ints durability = MinMaxBounds.Ints.ANY;
        private Optional<Holder<Potion>> potion = Optional.empty();
        private Optional<NbtPredicate> nbt = Optional.empty();

        private Builder() {
        }

        public static ItemPredicate.Builder item() {
            return new ItemPredicate.Builder();
        }

        public ItemPredicate.Builder of(ItemLike... items) {
            this.items = Optional.of(HolderSet.direct(item -> item.asItem().builtInRegistryHolder(), items));
            return this;
        }

        public ItemPredicate.Builder of(TagKey<Item> tag) {
            this.tag = Optional.of(tag);
            return this;
        }

        public ItemPredicate.Builder withCount(MinMaxBounds.Ints count) {
            this.count = count;
            return this;
        }

        public ItemPredicate.Builder hasDurability(MinMaxBounds.Ints durability) {
            this.durability = durability;
            return this;
        }

        public ItemPredicate.Builder isPotion(Potion potion) {
            this.potion = Optional.of(potion.builtInRegistryHolder());
            return this;
        }

        public ItemPredicate.Builder hasNbt(CompoundTag nbt) {
            this.nbt = Optional.of(new NbtPredicate(nbt));
            return this;
        }

        public ItemPredicate.Builder hasEnchantment(EnchantmentPredicate enchantment) {
            this.enchantments.add(enchantment);
            return this;
        }

        public ItemPredicate.Builder hasStoredEnchantment(EnchantmentPredicate enchantment) {
            this.storedEnchantments.add(enchantment);
            return this;
        }

        public ItemPredicate build() {
            List<EnchantmentPredicate> list = this.enchantments.build();
            List<EnchantmentPredicate> list2 = this.storedEnchantments.build();
            return new ItemPredicate(this.tag, this.items, this.count, this.durability, list, list2, this.potion, this.nbt);
        }
    }
}
