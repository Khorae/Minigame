package net.minecraft.data.loot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public abstract class BlockLootSubProvider implements LootTableSubProvider {
    protected static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(
        ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1)))
    );
    protected static final LootItemCondition.Builder HAS_NO_SILK_TOUCH = HAS_SILK_TOUCH.invert();
    protected static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
    private static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
    private static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
    protected final Set<Item> explosionResistant;
    protected final FeatureFlagSet enabledFeatures;
    protected final Map<ResourceLocation, LootTable.Builder> map;
    protected static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
    private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};

    protected BlockLootSubProvider(Set<Item> explosionImmuneItems, FeatureFlagSet requiredFeatures) {
        this(explosionImmuneItems, requiredFeatures, new HashMap<>());
    }

    protected BlockLootSubProvider(Set<Item> explosionImmuneItems, FeatureFlagSet requiredFeatures, Map<ResourceLocation, LootTable.Builder> lootTables) {
        this.explosionResistant = explosionImmuneItems;
        this.enabledFeatures = requiredFeatures;
        this.map = lootTables;
    }

    protected <T extends FunctionUserBuilder<T>> T applyExplosionDecay(ItemLike drop, FunctionUserBuilder<T> builder) {
        return !this.explosionResistant.contains(drop.asItem()) ? builder.apply(ApplyExplosionDecay.explosionDecay()) : builder.unwrap();
    }

    protected <T extends ConditionUserBuilder<T>> T applyExplosionCondition(ItemLike drop, ConditionUserBuilder<T> builder) {
        return !this.explosionResistant.contains(drop.asItem()) ? builder.when(ExplosionCondition.survivesExplosion()) : builder.unwrap();
    }

    public LootTable.Builder createSingleItemTable(ItemLike drop) {
        return LootTable.lootTable()
            .withPool(this.applyExplosionCondition(drop, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(drop))));
    }

    private static LootTable.Builder createSelfDropDispatchTable(
        Block drop, LootItemCondition.Builder conditionBuilder, LootPoolEntryContainer.Builder<?> child
    ) {
        return LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(drop).when(conditionBuilder).otherwise(child)));
    }

    protected static LootTable.Builder createSilkTouchDispatchTable(Block drop, LootPoolEntryContainer.Builder<?> child) {
        return createSelfDropDispatchTable(drop, HAS_SILK_TOUCH, child);
    }

    protected static LootTable.Builder createShearsDispatchTable(Block drop, LootPoolEntryContainer.Builder<?> child) {
        return createSelfDropDispatchTable(drop, HAS_SHEARS, child);
    }

    protected static LootTable.Builder createSilkTouchOrShearsDispatchTable(Block drop, LootPoolEntryContainer.Builder<?> child) {
        return createSelfDropDispatchTable(drop, HAS_SHEARS_OR_SILK_TOUCH, child);
    }

    protected LootTable.Builder createSingleItemTableWithSilkTouch(Block dropWithSilkTouch, ItemLike drop) {
        return createSilkTouchDispatchTable(
            dropWithSilkTouch, (LootPoolEntryContainer.Builder<?>)this.applyExplosionCondition(dropWithSilkTouch, LootItem.lootTableItem(drop))
        );
    }

    protected LootTable.Builder createSingleItemTable(ItemLike drop, NumberProvider count) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(
                        (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                            drop, LootItem.lootTableItem(drop).apply(SetItemCountFunction.setCount(count))
                        )
                    )
            );
    }

    protected LootTable.Builder createSingleItemTableWithSilkTouch(Block dropWithSilkTouch, ItemLike drop, NumberProvider count) {
        return createSilkTouchDispatchTable(
            dropWithSilkTouch,
            (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                dropWithSilkTouch, LootItem.lootTableItem(drop).apply(SetItemCountFunction.setCount(count))
            )
        );
    }

    private static LootTable.Builder createSilkTouchOnlyTable(ItemLike drop) {
        return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(drop)));
    }

    private LootTable.Builder createPotFlowerItemTable(ItemLike drop) {
        return LootTable.lootTable()
            .withPool(
                this.applyExplosionCondition(
                    Blocks.FLOWER_POT, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Blocks.FLOWER_POT))
                )
            )
            .withPool(this.applyExplosionCondition(drop, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(drop))));
    }

    protected LootTable.Builder createSlabItemTable(Block drop) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(
                        (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                            drop,
                            LootItem.lootTableItem(drop)
                                .apply(
                                    SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                                        .when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))
                                        )
                                )
                        )
                    )
            );
    }

    protected <T extends Comparable<T> & StringRepresentable> LootTable.Builder createSinglePropConditionTable(Block drop, Property<T> property, T value) {
        return LootTable.lootTable()
            .withPool(
                this.applyExplosionCondition(
                    drop,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(drop)
                                .when(
                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value))
                                )
                        )
                )
            );
    }

    protected LootTable.Builder createNameableBlockEntityTable(Block drop) {
        return LootTable.lootTable()
            .withPool(
                this.applyExplosionCondition(
                    drop,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(drop).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)))
                )
            );
    }

    protected LootTable.Builder createShulkerBoxDrop(Block drop) {
        return LootTable.lootTable()
            .withPool(
                this.applyExplosionCondition(
                    drop,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(drop)
                                .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                                .apply(
                                    CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                        .copy("Lock", "BlockEntityTag.Lock")
                                        .copy("LootTable", "BlockEntityTag.LootTable")
                                        .copy("LootTableSeed", "BlockEntityTag.LootTableSeed")
                                )
                                .apply(
                                    SetContainerContents.setContents(BlockEntityType.SHULKER_BOX).withEntry(DynamicLoot.dynamicEntry(ShulkerBoxBlock.CONTENTS))
                                )
                        )
                )
            );
    }

    protected LootTable.Builder createCopperOreDrops(Block drop) {
        return createSilkTouchDispatchTable(
            drop,
            (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                drop,
                LootItem.lootTableItem(Items.RAW_COPPER)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                    .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
            )
        );
    }

    protected LootTable.Builder createLapisOreDrops(Block drop) {
        return createSilkTouchDispatchTable(
            drop,
            (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                drop,
                LootItem.lootTableItem(Items.LAPIS_LAZULI)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 9.0F)))
                    .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
            )
        );
    }

    protected LootTable.Builder createRedstoneOreDrops(Block drop) {
        return createSilkTouchDispatchTable(
            drop,
            (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                drop,
                LootItem.lootTableItem(Items.REDSTONE)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 5.0F)))
                    .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))
            )
        );
    }

    protected LootTable.Builder createBannerDrop(Block drop) {
        return LootTable.lootTable()
            .withPool(
                this.applyExplosionCondition(
                    drop,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(drop)
                                .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Patterns", "BlockEntityTag.Patterns"))
                        )
                )
            );
    }

    protected static LootTable.Builder createBeeNestDrop(Block drop) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .when(HAS_SILK_TOUCH)
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(
                        LootItem.lootTableItem(drop)
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Bees", "BlockEntityTag.Bees"))
                            .apply(CopyBlockState.copyState(drop).copy(BeehiveBlock.HONEY_LEVEL))
                    )
            );
    }

    protected static LootTable.Builder createBeeHiveDrop(Block drop) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(
                        LootItem.lootTableItem(drop)
                            .when(HAS_SILK_TOUCH)
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Bees", "BlockEntityTag.Bees"))
                            .apply(CopyBlockState.copyState(drop).copy(BeehiveBlock.HONEY_LEVEL))
                            .otherwise(LootItem.lootTableItem(drop))
                    )
            );
    }

    protected static LootTable.Builder createCaveVinesDrop(Block drop) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.GLOW_BERRIES))
                    .when(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CaveVines.BERRIES, true))
                    )
            );
    }

    protected LootTable.Builder createOreDrop(Block dropWithSilkTouch, Item drop) {
        return createSilkTouchDispatchTable(
            dropWithSilkTouch,
            (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                dropWithSilkTouch, LootItem.lootTableItem(drop).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
            )
        );
    }

    protected LootTable.Builder createMushroomBlockDrop(Block dropWithSilkTouch, ItemLike drop) {
        return createSilkTouchDispatchTable(
            dropWithSilkTouch,
            (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                dropWithSilkTouch,
                LootItem.lootTableItem(drop)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(-6.0F, 2.0F)))
                    .apply(LimitCount.limitCount(IntRange.lowerBound(0)))
            )
        );
    }

    protected LootTable.Builder createGrassDrops(Block dropWithShears) {
        return createShearsDispatchTable(
            dropWithShears,
            (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                dropWithShears,
                LootItem.lootTableItem(Items.WHEAT_SEEDS)
                    .when(LootItemRandomChanceCondition.randomChance(0.125F))
                    .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 2))
            )
        );
    }

    public LootTable.Builder createStemDrops(Block stem, Item drop) {
        return LootTable.lootTable()
            .withPool(
                this.applyExplosionDecay(
                    stem,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(drop)
                                .apply(
                                    StemBlock.AGE.getPossibleValues(),
                                    integer -> SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, (float)(integer + 1) / 15.0F))
                                            .when(
                                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(stem)
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, integer.intValue()))
                                            )
                                )
                        )
                )
            );
    }

    public LootTable.Builder createAttachedStemDrops(Block stem, Item drop) {
        return LootTable.lootTable()
            .withPool(
                this.applyExplosionDecay(
                    stem,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(drop).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.53333336F))))
                )
            );
    }

    protected static LootTable.Builder createShearsOnlyDrop(ItemLike drop) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_SHEARS).add(LootItem.lootTableItem(drop)));
    }

    protected LootTable.Builder createMultifaceBlockDrops(Block drop, LootItemCondition.Builder condition) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .add(
                        (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                            drop,
                            LootItem.lootTableItem(drop)
                                .when(condition)
                                .apply(
                                    Direction.values(),
                                    direction -> SetItemCountFunction.setCount(ConstantValue.exactly(1.0F), true)
                                            .when(
                                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
                                                    .setProperties(
                                                        StatePropertiesPredicate.Builder.properties()
                                                            .hasProperty(MultifaceBlock.getFaceProperty(direction), true)
                                                    )
                                            )
                                )
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(-1.0F), true))
                        )
                    )
            );
    }

    protected LootTable.Builder createLeavesDrops(Block leaves, Block drop, float... chance) {
        return createSilkTouchOrShearsDispatchTable(
                leaves,
                ((LootPoolSingletonContainer.Builder)this.applyExplosionCondition(leaves, LootItem.lootTableItem(drop)))
                    .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, chance))
            )
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                    .add(
                        ((LootPoolSingletonContainer.Builder)this.applyExplosionDecay(
                                leaves, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                            ))
                            .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES))
                    )
            );
    }

    protected LootTable.Builder createOakLeavesDrops(Block leaves, Block drop, float... chance) {
        return this.createLeavesDrops(leaves, drop, chance)
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                    .add(
                        ((LootPoolSingletonContainer.Builder)this.applyExplosionCondition(leaves, LootItem.lootTableItem(Items.APPLE)))
                            .when(
                                BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)
                            )
                    )
            );
    }

    protected LootTable.Builder createMangroveLeavesDrops(Block leaves) {
        return createSilkTouchOrShearsDispatchTable(
            leaves,
            ((LootPoolSingletonContainer.Builder)this.applyExplosionDecay(
                    Blocks.MANGROVE_LEAVES, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                ))
                .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES))
        );
    }

    protected LootTable.Builder createCropDrops(Block crop, Item product, Item seeds, LootItemCondition.Builder condition) {
        return this.applyExplosionDecay(
            crop,
            LootTable.lootTable()
                .withPool(LootPool.lootPool().add(LootItem.lootTableItem(product).when(condition).otherwise(LootItem.lootTableItem(seeds))))
                .withPool(
                    LootPool.lootPool()
                        .when(condition)
                        .add(LootItem.lootTableItem(seeds).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))
                )
        );
    }

    protected static LootTable.Builder createDoublePlantShearsDrop(Block seagrass) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool().when(HAS_SHEARS).add(LootItem.lootTableItem(seagrass).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))))
            );
    }

    protected LootTable.Builder createDoublePlantWithSeedDrops(Block tallPlant, Block shortPlant) {
        LootPoolEntryContainer.Builder<?> builder = LootItem.lootTableItem(shortPlant)
            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
            .when(HAS_SHEARS)
            .otherwise(
                ((LootPoolSingletonContainer.Builder)this.applyExplosionCondition(tallPlant, LootItem.lootTableItem(Items.WHEAT_SEEDS)))
                    .when(LootItemRandomChanceCondition.randomChance(0.125F))
            );
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .add(builder)
                    .when(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(tallPlant)
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
                    )
                    .when(
                        LocationCheck.checkLocation(
                            LocationPredicate.Builder.location()
                                .setBlock(
                                    BlockPredicate.Builder.block()
                                        .of(tallPlant)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))
                                ),
                            new BlockPos(0, 1, 0)
                        )
                    )
            )
            .withPool(
                LootPool.lootPool()
                    .add(builder)
                    .when(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(tallPlant)
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))
                    )
                    .when(
                        LocationCheck.checkLocation(
                            LocationPredicate.Builder.location()
                                .setBlock(
                                    BlockPredicate.Builder.block()
                                        .of(tallPlant)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
                                ),
                            new BlockPos(0, -1, 0)
                        )
                    )
            );
    }

    protected LootTable.Builder createCandleDrops(Block candle) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(
                        (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                            candle,
                            LootItem.lootTableItem(candle)
                                .apply(
                                    List.of(2, 3, 4),
                                    candles -> SetItemCountFunction.setCount(ConstantValue.exactly((float)candles.intValue()))
                                            .when(
                                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(candle)
                                                    .setProperties(
                                                        StatePropertiesPredicate.Builder.properties().hasProperty(CandleBlock.CANDLES, candles.intValue())
                                                    )
                                            )
                                )
                        )
                    )
            );
    }

    protected LootTable.Builder createPetalsDrops(Block flowerbed) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(
                        (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                            flowerbed,
                            LootItem.lootTableItem(flowerbed)
                                .apply(
                                    IntStream.rangeClosed(1, 4).boxed().toList(),
                                    flowerAmount -> SetItemCountFunction.setCount(ConstantValue.exactly((float)flowerAmount.intValue()))
                                            .when(
                                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(flowerbed)
                                                    .setProperties(
                                                        StatePropertiesPredicate.Builder.properties()
                                                            .hasProperty(PinkPetalsBlock.AMOUNT, flowerAmount.intValue())
                                                    )
                                            )
                                )
                        )
                    )
            );
    }

    protected static LootTable.Builder createCandleCakeDrops(Block candleCake) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(candleCake)));
    }

    public static LootTable.Builder noDrop() {
        return LootTable.lootTable();
    }

    protected abstract void generate();

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> exporter) {
        this.generate();
        Set<ResourceLocation> set = new HashSet<>();

        for (Block block : BuiltInRegistries.BLOCK) {
            if (block.isEnabled(this.enabledFeatures)) {
                ResourceLocation resourceLocation = block.getLootTable();
                if (resourceLocation != BuiltInLootTables.EMPTY && set.add(resourceLocation)) {
                    LootTable.Builder builder = this.map.remove(resourceLocation);
                    if (builder == null) {
                        throw new IllegalStateException(
                            String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourceLocation, BuiltInRegistries.BLOCK.getKey(block))
                        );
                    }

                    exporter.accept(resourceLocation, builder);
                }
            }
        }

        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
        }
    }

    protected void addNetherVinesDropTable(Block block, Block drop) {
        LootTable.Builder builder = createSilkTouchOrShearsDispatchTable(
            block, LootItem.lootTableItem(block).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.33F, 0.55F, 0.77F, 1.0F))
        );
        this.add(block, builder);
        this.add(drop, builder);
    }

    protected LootTable.Builder createDoorTable(Block block) {
        return this.createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER);
    }

    protected void dropPottedContents(Block block) {
        this.add(block, flowerPot -> this.createPotFlowerItemTable(((FlowerPotBlock)flowerPot).getPotted()));
    }

    protected void otherWhenSilkTouch(Block block, Block drop) {
        this.add(block, createSilkTouchOnlyTable(drop));
    }

    protected void dropOther(Block block, ItemLike drop) {
        this.add(block, this.createSingleItemTable(drop));
    }

    protected void dropWhenSilkTouch(Block block) {
        this.otherWhenSilkTouch(block, block);
    }

    protected void dropSelf(Block block) {
        this.dropOther(block, block);
    }

    protected void add(Block block, Function<Block, LootTable.Builder> lootTableFunction) {
        this.add(block, lootTableFunction.apply(block));
    }

    protected void add(Block block, LootTable.Builder lootTable) {
        this.map.put(block.getLootTable(), lootTable);
    }
}
