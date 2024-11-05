package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public abstract class EntityLootSubProvider implements LootTableSubProvider {
    protected static final EntityPredicate.Builder ENTITY_ON_FIRE = EntityPredicate.Builder.entity()
        .flags(EntityFlagsPredicate.Builder.flags().setOnFire(true));
    private static final Set<EntityType<?>> SPECIAL_LOOT_TABLE_TYPES = ImmutableSet.of(
        EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER
    );
    private final FeatureFlagSet allowed;
    private final FeatureFlagSet required;
    private final Map<EntityType<?>, Map<ResourceLocation, LootTable.Builder>> map = Maps.newHashMap();

    protected EntityLootSubProvider(FeatureFlagSet requiredFeatures) {
        this(requiredFeatures, requiredFeatures);
    }

    protected EntityLootSubProvider(FeatureFlagSet requiredFeatures, FeatureFlagSet featureSet) {
        this.allowed = requiredFeatures;
        this.required = featureSet;
    }

    protected static LootTable.Builder createSheepTable(ItemLike item) {
        return LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(item)))
            .withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootTableReference.lootTableReference(EntityType.SHEEP.getDefaultLootTable()))
            );
    }

    public abstract void generate();

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> exporter) {
        this.generate();
        Set<ResourceLocation> set = Sets.newHashSet();
        BuiltInRegistries.ENTITY_TYPE
            .holders()
            .forEach(
                entityType -> {
                    EntityType<?> entityType2 = entityType.value();
                    if (entityType2.isEnabled(this.allowed)) {
                        if (canHaveLootTable(entityType2)) {
                            Map<ResourceLocation, LootTable.Builder> map = this.map.remove(entityType2);
                            ResourceLocation resourceLocation = entityType2.getDefaultLootTable();
                            if (!resourceLocation.equals(BuiltInLootTables.EMPTY)
                                && entityType2.isEnabled(this.required)
                                && (map == null || !map.containsKey(resourceLocation))) {
                                throw new IllegalStateException(
                                    String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourceLocation, entityType.key().location())
                                );
                            }

                            if (map != null) {
                                map.forEach(
                                    (lootTableId, lootTableBuilder) -> {
                                        if (!set.add(lootTableId)) {
                                            throw new IllegalStateException(
                                                String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", lootTableId, entityType.key().location())
                                            );
                                        } else {
                                            exporter.accept(lootTableId, lootTableBuilder);
                                        }
                                    }
                                );
                            }
                        } else {
                            Map<ResourceLocation, LootTable.Builder> map2 = this.map.remove(entityType2);
                            if (map2 != null) {
                                throw new IllegalStateException(
                                    String.format(
                                        Locale.ROOT,
                                        "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot",
                                        map2.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")),
                                        entityType.key().location()
                                    )
                                );
                            }
                        }
                    }
                }
            );
        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
        }
    }

    private static boolean canHaveLootTable(EntityType<?> entityType) {
        return SPECIAL_LOOT_TABLE_TYPES.contains(entityType) || entityType.getCategory() != MobCategory.MISC;
    }

    protected LootItemCondition.Builder killedByFrog() {
        return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG)));
    }

    protected LootItemCondition.Builder killedByFrogVariant(FrogVariant variant) {
        return DamageSourceCondition.hasDamageSource(
            DamageSourcePredicate.Builder.damageType()
                .source(EntityPredicate.Builder.entity().of(EntityType.FROG).subPredicate(EntitySubPredicate.variant(variant)))
        );
    }

    protected void add(EntityType<?> entityType, LootTable.Builder lootTable) {
        this.add(entityType, entityType.getDefaultLootTable(), lootTable);
    }

    protected void add(EntityType<?> entityType, ResourceLocation entityId, LootTable.Builder lootTable) {
        this.map.computeIfAbsent(entityType, type -> new HashMap<>()).put(entityId, lootTable);
    }
}
