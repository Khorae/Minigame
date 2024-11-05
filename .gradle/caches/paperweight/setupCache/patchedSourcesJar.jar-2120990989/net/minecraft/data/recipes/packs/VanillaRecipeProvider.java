package net.minecraft.data.recipes.packs;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.DecoratedPotRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.FireworkStarFadeRecipe;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.MapCloningRecipe;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.crafting.ShieldDecorationRecipe;
import net.minecraft.world.item.crafting.ShulkerBoxColoring;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.SuspiciousStewRecipe;
import net.minecraft.world.item.crafting.TippedArrowRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class VanillaRecipeProvider extends RecipeProvider {
    private static final ImmutableList<ItemLike> COAL_SMELTABLES = ImmutableList.of(Items.COAL_ORE, Items.DEEPSLATE_COAL_ORE);
    private static final ImmutableList<ItemLike> IRON_SMELTABLES = ImmutableList.of(Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON);
    private static final ImmutableList<ItemLike> COPPER_SMELTABLES = ImmutableList.of(Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER);
    private static final ImmutableList<ItemLike> GOLD_SMELTABLES = ImmutableList.of(
        Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.NETHER_GOLD_ORE, Items.RAW_GOLD
    );
    private static final ImmutableList<ItemLike> DIAMOND_SMELTABLES = ImmutableList.of(Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE);
    private static final ImmutableList<ItemLike> LAPIS_SMELTABLES = ImmutableList.of(Items.LAPIS_ORE, Items.DEEPSLATE_LAPIS_ORE);
    private static final ImmutableList<ItemLike> REDSTONE_SMELTABLES = ImmutableList.of(Items.REDSTONE_ORE, Items.DEEPSLATE_REDSTONE_ORE);
    private static final ImmutableList<ItemLike> EMERALD_SMELTABLES = ImmutableList.of(Items.EMERALD_ORE, Items.DEEPSLATE_EMERALD_ORE);

    public VanillaRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        return CompletableFuture.allOf(
            super.run(writer),
            this.buildAdvancement(
                writer,
                Advancement.Builder.recipeAdvancement()
                    .addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                    .build(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT)
            )
        );
    }

    @Override
    protected void buildRecipes(RecipeOutput exporter) {
        generateForEnabledBlockFamilies(exporter, FeatureFlagSet.of(FeatureFlags.VANILLA));
        planksFromLog(exporter, Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS, 4);
        planksFromLogs(exporter, Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS, 4);
        planksFromLogs(exporter, Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS, 4);
        planksFromLog(exporter, Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS, 4);
        planksFromLogs(exporter, Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS, 4);
        planksFromLogs(exporter, Blocks.OAK_PLANKS, ItemTags.OAK_LOGS, 4);
        planksFromLogs(exporter, Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS, 4);
        planksFromLogs(exporter, Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS, 4);
        planksFromLogs(exporter, Blocks.MANGROVE_PLANKS, ItemTags.MANGROVE_LOGS, 4);
        woodFromLogs(exporter, Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
        woodFromLogs(exporter, Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
        woodFromLogs(exporter, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
        woodFromLogs(exporter, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
        woodFromLogs(exporter, Blocks.OAK_WOOD, Blocks.OAK_LOG);
        woodFromLogs(exporter, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
        woodFromLogs(exporter, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
        woodFromLogs(exporter, Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
        woodFromLogs(exporter, Blocks.MANGROVE_WOOD, Blocks.MANGROVE_LOG);
        woodFromLogs(exporter, Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
        woodFromLogs(exporter, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
        woodFromLogs(exporter, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
        woodFromLogs(exporter, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
        woodFromLogs(exporter, Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
        woodFromLogs(exporter, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
        woodFromLogs(exporter, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
        woodFromLogs(exporter, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
        woodFromLogs(exporter, Blocks.STRIPPED_MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG);
        woodenBoat(exporter, Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
        woodenBoat(exporter, Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
        woodenBoat(exporter, Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
        woodenBoat(exporter, Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
        woodenBoat(exporter, Items.OAK_BOAT, Blocks.OAK_PLANKS);
        woodenBoat(exporter, Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
        woodenBoat(exporter, Items.MANGROVE_BOAT, Blocks.MANGROVE_PLANKS);
        List<Item> list = List.of(
            Items.BLACK_DYE,
            Items.BLUE_DYE,
            Items.BROWN_DYE,
            Items.CYAN_DYE,
            Items.GRAY_DYE,
            Items.GREEN_DYE,
            Items.LIGHT_BLUE_DYE,
            Items.LIGHT_GRAY_DYE,
            Items.LIME_DYE,
            Items.MAGENTA_DYE,
            Items.ORANGE_DYE,
            Items.PINK_DYE,
            Items.PURPLE_DYE,
            Items.RED_DYE,
            Items.YELLOW_DYE,
            Items.WHITE_DYE
        );
        List<Item> list2 = List.of(
            Items.BLACK_WOOL,
            Items.BLUE_WOOL,
            Items.BROWN_WOOL,
            Items.CYAN_WOOL,
            Items.GRAY_WOOL,
            Items.GREEN_WOOL,
            Items.LIGHT_BLUE_WOOL,
            Items.LIGHT_GRAY_WOOL,
            Items.LIME_WOOL,
            Items.MAGENTA_WOOL,
            Items.ORANGE_WOOL,
            Items.PINK_WOOL,
            Items.PURPLE_WOOL,
            Items.RED_WOOL,
            Items.YELLOW_WOOL,
            Items.WHITE_WOOL
        );
        List<Item> list3 = List.of(
            Items.BLACK_BED,
            Items.BLUE_BED,
            Items.BROWN_BED,
            Items.CYAN_BED,
            Items.GRAY_BED,
            Items.GREEN_BED,
            Items.LIGHT_BLUE_BED,
            Items.LIGHT_GRAY_BED,
            Items.LIME_BED,
            Items.MAGENTA_BED,
            Items.ORANGE_BED,
            Items.PINK_BED,
            Items.PURPLE_BED,
            Items.RED_BED,
            Items.YELLOW_BED,
            Items.WHITE_BED
        );
        List<Item> list4 = List.of(
            Items.BLACK_CARPET,
            Items.BLUE_CARPET,
            Items.BROWN_CARPET,
            Items.CYAN_CARPET,
            Items.GRAY_CARPET,
            Items.GREEN_CARPET,
            Items.LIGHT_BLUE_CARPET,
            Items.LIGHT_GRAY_CARPET,
            Items.LIME_CARPET,
            Items.MAGENTA_CARPET,
            Items.ORANGE_CARPET,
            Items.PINK_CARPET,
            Items.PURPLE_CARPET,
            Items.RED_CARPET,
            Items.YELLOW_CARPET,
            Items.WHITE_CARPET
        );
        colorBlockWithDye(exporter, list, list2, "wool");
        colorBlockWithDye(exporter, list, list3, "bed");
        colorBlockWithDye(exporter, list, list4, "carpet");
        carpet(exporter, Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
        bedFromPlanksAndWool(exporter, Items.BLACK_BED, Blocks.BLACK_WOOL);
        banner(exporter, Items.BLACK_BANNER, Blocks.BLACK_WOOL);
        carpet(exporter, Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
        bedFromPlanksAndWool(exporter, Items.BLUE_BED, Blocks.BLUE_WOOL);
        banner(exporter, Items.BLUE_BANNER, Blocks.BLUE_WOOL);
        carpet(exporter, Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
        bedFromPlanksAndWool(exporter, Items.BROWN_BED, Blocks.BROWN_WOOL);
        banner(exporter, Items.BROWN_BANNER, Blocks.BROWN_WOOL);
        carpet(exporter, Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
        bedFromPlanksAndWool(exporter, Items.CYAN_BED, Blocks.CYAN_WOOL);
        banner(exporter, Items.CYAN_BANNER, Blocks.CYAN_WOOL);
        carpet(exporter, Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
        bedFromPlanksAndWool(exporter, Items.GRAY_BED, Blocks.GRAY_WOOL);
        banner(exporter, Items.GRAY_BANNER, Blocks.GRAY_WOOL);
        carpet(exporter, Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
        bedFromPlanksAndWool(exporter, Items.GREEN_BED, Blocks.GREEN_WOOL);
        banner(exporter, Items.GREEN_BANNER, Blocks.GREEN_WOOL);
        carpet(exporter, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
        bedFromPlanksAndWool(exporter, Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
        banner(exporter, Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
        carpet(exporter, Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
        bedFromPlanksAndWool(exporter, Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
        banner(exporter, Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
        carpet(exporter, Blocks.LIME_CARPET, Blocks.LIME_WOOL);
        bedFromPlanksAndWool(exporter, Items.LIME_BED, Blocks.LIME_WOOL);
        banner(exporter, Items.LIME_BANNER, Blocks.LIME_WOOL);
        carpet(exporter, Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
        bedFromPlanksAndWool(exporter, Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
        banner(exporter, Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
        carpet(exporter, Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
        bedFromPlanksAndWool(exporter, Items.ORANGE_BED, Blocks.ORANGE_WOOL);
        banner(exporter, Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
        carpet(exporter, Blocks.PINK_CARPET, Blocks.PINK_WOOL);
        bedFromPlanksAndWool(exporter, Items.PINK_BED, Blocks.PINK_WOOL);
        banner(exporter, Items.PINK_BANNER, Blocks.PINK_WOOL);
        carpet(exporter, Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
        bedFromPlanksAndWool(exporter, Items.PURPLE_BED, Blocks.PURPLE_WOOL);
        banner(exporter, Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
        carpet(exporter, Blocks.RED_CARPET, Blocks.RED_WOOL);
        bedFromPlanksAndWool(exporter, Items.RED_BED, Blocks.RED_WOOL);
        banner(exporter, Items.RED_BANNER, Blocks.RED_WOOL);
        carpet(exporter, Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
        bedFromPlanksAndWool(exporter, Items.WHITE_BED, Blocks.WHITE_WOOL);
        banner(exporter, Items.WHITE_BANNER, Blocks.WHITE_WOOL);
        carpet(exporter, Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
        bedFromPlanksAndWool(exporter, Items.YELLOW_BED, Blocks.YELLOW_WOOL);
        banner(exporter, Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
        carpet(exporter, Blocks.MOSS_CARPET, Blocks.MOSS_BLOCK);
        stainedGlassFromGlassAndDye(exporter, Blocks.BLACK_STAINED_GLASS, Items.BLACK_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.BLACK_STAINED_GLASS_PANE, Items.BLACK_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.BLUE_STAINED_GLASS, Items.BLUE_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.BLUE_STAINED_GLASS_PANE, Items.BLUE_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.BROWN_STAINED_GLASS, Items.BROWN_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.BROWN_STAINED_GLASS_PANE, Items.BROWN_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.CYAN_STAINED_GLASS, Items.CYAN_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.CYAN_STAINED_GLASS_PANE, Items.CYAN_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.GRAY_STAINED_GLASS, Items.GRAY_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.GRAY_STAINED_GLASS_PANE, Items.GRAY_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.GREEN_STAINED_GLASS, Items.GREEN_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.GREEN_STAINED_GLASS_PANE, Items.GREEN_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.LIGHT_BLUE_STAINED_GLASS, Items.LIGHT_BLUE_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Items.LIGHT_BLUE_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.LIGHT_GRAY_STAINED_GLASS, Items.LIGHT_GRAY_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.LIME_STAINED_GLASS, Items.LIME_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.LIME_STAINED_GLASS_PANE, Items.LIME_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.MAGENTA_STAINED_GLASS, Items.MAGENTA_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.MAGENTA_STAINED_GLASS_PANE, Items.MAGENTA_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.ORANGE_STAINED_GLASS, Items.ORANGE_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.ORANGE_STAINED_GLASS_PANE, Items.ORANGE_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.PINK_STAINED_GLASS, Items.PINK_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.PINK_STAINED_GLASS_PANE, Items.PINK_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.PURPLE_STAINED_GLASS, Items.PURPLE_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.PURPLE_STAINED_GLASS_PANE, Items.PURPLE_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.RED_STAINED_GLASS, Items.RED_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.RED_STAINED_GLASS_PANE, Items.RED_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.WHITE_STAINED_GLASS, Items.WHITE_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.WHITE_STAINED_GLASS_PANE, Items.WHITE_DYE);
        stainedGlassFromGlassAndDye(exporter, Blocks.YELLOW_STAINED_GLASS, Items.YELLOW_DYE);
        stainedGlassPaneFromStainedGlass(exporter, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS);
        stainedGlassPaneFromGlassPaneAndDye(exporter, Blocks.YELLOW_STAINED_GLASS_PANE, Items.YELLOW_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.BLACK_TERRACOTTA, Items.BLACK_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.BLUE_TERRACOTTA, Items.BLUE_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.BROWN_TERRACOTTA, Items.BROWN_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.CYAN_TERRACOTTA, Items.CYAN_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.GRAY_TERRACOTTA, Items.GRAY_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.GREEN_TERRACOTTA, Items.GREEN_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.LIME_TERRACOTTA, Items.LIME_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.MAGENTA_TERRACOTTA, Items.MAGENTA_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.ORANGE_TERRACOTTA, Items.ORANGE_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.PINK_TERRACOTTA, Items.PINK_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.PURPLE_TERRACOTTA, Items.PURPLE_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.RED_TERRACOTTA, Items.RED_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.WHITE_TERRACOTTA, Items.WHITE_DYE);
        coloredTerracottaFromTerracottaAndDye(exporter, Blocks.YELLOW_TERRACOTTA, Items.YELLOW_DYE);
        concretePowder(exporter, Blocks.BLACK_CONCRETE_POWDER, Items.BLACK_DYE);
        concretePowder(exporter, Blocks.BLUE_CONCRETE_POWDER, Items.BLUE_DYE);
        concretePowder(exporter, Blocks.BROWN_CONCRETE_POWDER, Items.BROWN_DYE);
        concretePowder(exporter, Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE);
        concretePowder(exporter, Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE);
        concretePowder(exporter, Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE);
        concretePowder(exporter, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE);
        concretePowder(exporter, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE);
        concretePowder(exporter, Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE);
        concretePowder(exporter, Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE);
        concretePowder(exporter, Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE);
        concretePowder(exporter, Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE);
        concretePowder(exporter, Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE);
        concretePowder(exporter, Blocks.RED_CONCRETE_POWDER, Items.RED_DYE);
        concretePowder(exporter, Blocks.WHITE_CONCRETE_POWDER, Items.WHITE_DYE);
        concretePowder(exporter, Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.CANDLE)
            .define('S', Items.STRING)
            .define('H', Items.HONEYCOMB)
            .pattern("S")
            .pattern("H")
            .unlockedBy("has_string", has(Items.STRING))
            .unlockedBy("has_honeycomb", has(Items.HONEYCOMB))
            .save(exporter);
        candle(exporter, Blocks.BLACK_CANDLE, Items.BLACK_DYE);
        candle(exporter, Blocks.BLUE_CANDLE, Items.BLUE_DYE);
        candle(exporter, Blocks.BROWN_CANDLE, Items.BROWN_DYE);
        candle(exporter, Blocks.CYAN_CANDLE, Items.CYAN_DYE);
        candle(exporter, Blocks.GRAY_CANDLE, Items.GRAY_DYE);
        candle(exporter, Blocks.GREEN_CANDLE, Items.GREEN_DYE);
        candle(exporter, Blocks.LIGHT_BLUE_CANDLE, Items.LIGHT_BLUE_DYE);
        candle(exporter, Blocks.LIGHT_GRAY_CANDLE, Items.LIGHT_GRAY_DYE);
        candle(exporter, Blocks.LIME_CANDLE, Items.LIME_DYE);
        candle(exporter, Blocks.MAGENTA_CANDLE, Items.MAGENTA_DYE);
        candle(exporter, Blocks.ORANGE_CANDLE, Items.ORANGE_DYE);
        candle(exporter, Blocks.PINK_CANDLE, Items.PINK_DYE);
        candle(exporter, Blocks.PURPLE_CANDLE, Items.PURPLE_DYE);
        candle(exporter, Blocks.RED_CANDLE, Items.RED_DYE);
        candle(exporter, Blocks.WHITE_CANDLE, Items.WHITE_DYE);
        candle(exporter, Blocks.YELLOW_CANDLE, Items.YELLOW_DYE);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.PACKED_MUD, 1)
            .requires(Blocks.MUD)
            .requires(Items.WHEAT)
            .unlockedBy("has_mud", has(Blocks.MUD))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.MUD_BRICKS, 4)
            .define('#', Blocks.PACKED_MUD)
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_packed_mud", has(Blocks.PACKED_MUD))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.MUDDY_MANGROVE_ROOTS, 1)
            .requires(Blocks.MUD)
            .requires(Items.MANGROVE_ROOTS)
            .unlockedBy("has_mangrove_roots", has(Blocks.MANGROVE_ROOTS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Blocks.ACTIVATOR_RAIL, 6)
            .define('#', Blocks.REDSTONE_TORCH)
            .define('S', Items.STICK)
            .define('X', Items.IRON_INGOT)
            .pattern("XSX")
            .pattern("X#X")
            .pattern("XSX")
            .unlockedBy("has_rail", has(Blocks.RAIL))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.ANDESITE, 2)
            .requires(Blocks.DIORITE)
            .requires(Blocks.COBBLESTONE)
            .unlockedBy("has_stone", has(Blocks.DIORITE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.ANVIL)
            .define('I', Blocks.IRON_BLOCK)
            .define('i', Items.IRON_INGOT)
            .pattern("III")
            .pattern(" i ")
            .pattern("iii")
            .unlockedBy("has_iron_block", has(Blocks.IRON_BLOCK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.ARMOR_STAND)
            .define('/', Items.STICK)
            .define('_', Blocks.SMOOTH_STONE_SLAB)
            .pattern("///")
            .pattern(" / ")
            .pattern("/_/")
            .unlockedBy("has_stone_slab", has(Blocks.SMOOTH_STONE_SLAB))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.ARROW, 4)
            .define('#', Items.STICK)
            .define('X', Items.FLINT)
            .define('Y', Items.FEATHER)
            .pattern("X")
            .pattern("#")
            .pattern("Y")
            .unlockedBy("has_feather", has(Items.FEATHER))
            .unlockedBy("has_flint", has(Items.FLINT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.BARREL, 1)
            .define('P', ItemTags.PLANKS)
            .define('S', ItemTags.WOODEN_SLABS)
            .pattern("PSP")
            .pattern("P P")
            .pattern("PSP")
            .unlockedBy("has_planks", has(ItemTags.PLANKS))
            .unlockedBy("has_wood_slab", has(ItemTags.WOODEN_SLABS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.BEACON)
            .define('S', Items.NETHER_STAR)
            .define('G', Blocks.GLASS)
            .define('O', Blocks.OBSIDIAN)
            .pattern("GGG")
            .pattern("GSG")
            .pattern("OOO")
            .unlockedBy("has_nether_star", has(Items.NETHER_STAR))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.BEEHIVE)
            .define('P', ItemTags.PLANKS)
            .define('H', Items.HONEYCOMB)
            .pattern("PPP")
            .pattern("HHH")
            .pattern("PPP")
            .unlockedBy("has_honeycomb", has(Items.HONEYCOMB))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.BEETROOT_SOUP)
            .requires(Items.BOWL)
            .requires(Items.BEETROOT, 6)
            .unlockedBy("has_beetroot", has(Items.BEETROOT))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BLACK_DYE)
            .requires(Items.INK_SAC)
            .group("black_dye")
            .unlockedBy("has_ink_sac", has(Items.INK_SAC))
            .save(exporter);
        oneToOneConversionRecipe(exporter, Items.BLACK_DYE, Blocks.WITHER_ROSE, "black_dye");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BREWING, Items.BLAZE_POWDER, 2)
            .requires(Items.BLAZE_ROD)
            .unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BLUE_DYE)
            .requires(Items.LAPIS_LAZULI)
            .group("blue_dye")
            .unlockedBy("has_lapis_lazuli", has(Items.LAPIS_LAZULI))
            .save(exporter);
        oneToOneConversionRecipe(exporter, Items.BLUE_DYE, Blocks.CORNFLOWER, "blue_dye");
        threeByThreePacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BLUE_ICE, Blocks.PACKED_ICE);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 3)
            .requires(Items.BONE)
            .group("bonemeal")
            .unlockedBy("has_bone", has(Items.BONE))
            .save(exporter);
        nineBlockStorageRecipesRecipesWithCustomUnpacking(
            exporter, RecipeCategory.MISC, Items.BONE_MEAL, RecipeCategory.BUILDING_BLOCKS, Items.BONE_BLOCK, "bone_meal_from_bone_block", "bonemeal"
        );
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BOOK)
            .requires(Items.PAPER, 3)
            .requires(Items.LEATHER)
            .unlockedBy("has_paper", has(Items.PAPER))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.BOOKSHELF)
            .define('#', ItemTags.PLANKS)
            .define('X', Items.BOOK)
            .pattern("###")
            .pattern("XXX")
            .pattern("###")
            .unlockedBy("has_book", has(Items.BOOK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.BOW)
            .define('#', Items.STICK)
            .define('X', Items.STRING)
            .pattern(" #X")
            .pattern("# X")
            .pattern(" #X")
            .unlockedBy("has_string", has(Items.STRING))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BOWL, 4)
            .define('#', ItemTags.PLANKS)
            .pattern("# #")
            .pattern(" # ")
            .unlockedBy("has_brown_mushroom", has(Blocks.BROWN_MUSHROOM))
            .unlockedBy("has_red_mushroom", has(Blocks.RED_MUSHROOM))
            .unlockedBy("has_mushroom_stew", has(Items.MUSHROOM_STEW))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Items.BREAD)
            .define('#', Items.WHEAT)
            .pattern("###")
            .unlockedBy("has_wheat", has(Items.WHEAT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Blocks.BREWING_STAND)
            .define('B', Items.BLAZE_ROD)
            .define('#', ItemTags.STONE_CRAFTING_MATERIALS)
            .pattern(" B ")
            .pattern("###")
            .unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.BRICKS)
            .define('#', Items.BRICK)
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_brick", has(Items.BRICK))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BROWN_DYE)
            .requires(Items.COCOA_BEANS)
            .group("brown_dye")
            .unlockedBy("has_cocoa_beans", has(Items.COCOA_BEANS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BUCKET)
            .define('#', Items.IRON_INGOT)
            .pattern("# #")
            .pattern(" # ")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Blocks.CAKE)
            .define('A', Items.MILK_BUCKET)
            .define('B', Items.SUGAR)
            .define('C', Items.WHEAT)
            .define('E', Items.EGG)
            .pattern("AAA")
            .pattern("BEB")
            .pattern("CCC")
            .unlockedBy("has_egg", has(Items.EGG))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.CAMPFIRE)
            .define('L', ItemTags.LOGS)
            .define('S', Items.STICK)
            .define('C', ItemTags.COALS)
            .pattern(" S ")
            .pattern("SCS")
            .pattern("LLL")
            .unlockedBy("has_stick", has(Items.STICK))
            .unlockedBy("has_coal", has(ItemTags.COALS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Items.CARROT_ON_A_STICK)
            .define('#', Items.FISHING_ROD)
            .define('X', Items.CARROT)
            .pattern("# ")
            .pattern(" X")
            .unlockedBy("has_carrot", has(Items.CARROT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Items.WARPED_FUNGUS_ON_A_STICK)
            .define('#', Items.FISHING_ROD)
            .define('X', Items.WARPED_FUNGUS)
            .pattern("# ")
            .pattern(" X")
            .unlockedBy("has_warped_fungus", has(Items.WARPED_FUNGUS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Blocks.CAULDRON)
            .define('#', Items.IRON_INGOT)
            .pattern("# #")
            .pattern("# #")
            .pattern("###")
            .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.COMPOSTER)
            .define('#', ItemTags.WOODEN_SLABS)
            .pattern("# #")
            .pattern("# #")
            .pattern("###")
            .unlockedBy("has_wood_slab", has(ItemTags.WOODEN_SLABS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.CHEST)
            .define('#', ItemTags.PLANKS)
            .pattern("###")
            .pattern("# #")
            .pattern("###")
            .unlockedBy(
                "has_lots_of_items",
                CriteriaTriggers.INVENTORY_CHANGED
                    .createCriterion(
                        new InventoryChangeTrigger.TriggerInstance(
                            Optional.empty(),
                            new InventoryChangeTrigger.TriggerInstance.Slots(MinMaxBounds.Ints.atLeast(10), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY),
                            List.of()
                        )
                    )
            )
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, Items.CHEST_MINECART)
            .requires(Blocks.CHEST)
            .requires(Items.MINECART)
            .unlockedBy("has_minecart", has(Items.MINECART))
            .save(exporter);
        chestBoat(exporter, Items.ACACIA_CHEST_BOAT, Items.ACACIA_BOAT);
        chestBoat(exporter, Items.BIRCH_CHEST_BOAT, Items.BIRCH_BOAT);
        chestBoat(exporter, Items.DARK_OAK_CHEST_BOAT, Items.DARK_OAK_BOAT);
        chestBoat(exporter, Items.JUNGLE_CHEST_BOAT, Items.JUNGLE_BOAT);
        chestBoat(exporter, Items.OAK_CHEST_BOAT, Items.OAK_BOAT);
        chestBoat(exporter, Items.SPRUCE_CHEST_BOAT, Items.SPRUCE_BOAT);
        chestBoat(exporter, Items.MANGROVE_CHEST_BOAT, Items.MANGROVE_BOAT);
        chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_QUARTZ_BLOCK, Ingredient.of(Blocks.QUARTZ_SLAB))
            .unlockedBy("has_chiseled_quartz_block", has(Blocks.CHISELED_QUARTZ_BLOCK))
            .unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK))
            .unlockedBy("has_quartz_pillar", has(Blocks.QUARTZ_PILLAR))
            .save(exporter);
        chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_STONE_BRICKS, Ingredient.of(Blocks.STONE_BRICK_SLAB))
            .unlockedBy("has_tag", has(ItemTags.STONE_BRICKS))
            .save(exporter);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CLAY, Items.CLAY_BALL);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.CLOCK)
            .define('#', Items.GOLD_INGOT)
            .define('X', Items.REDSTONE)
            .pattern(" # ")
            .pattern("#X#")
            .pattern(" # ")
            .unlockedBy("has_redstone", has(Items.REDSTONE))
            .save(exporter);
        nineBlockStorageRecipes(exporter, RecipeCategory.MISC, Items.COAL, RecipeCategory.BUILDING_BLOCKS, Items.COAL_BLOCK);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.COARSE_DIRT, 4)
            .define('D', Blocks.DIRT)
            .define('G', Blocks.GRAVEL)
            .pattern("DG")
            .pattern("GD")
            .unlockedBy("has_gravel", has(Blocks.GRAVEL))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.COMPARATOR)
            .define('#', Blocks.REDSTONE_TORCH)
            .define('X', Items.QUARTZ)
            .define('I', Blocks.STONE)
            .pattern(" # ")
            .pattern("#X#")
            .pattern("III")
            .unlockedBy("has_quartz", has(Items.QUARTZ))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.COMPASS)
            .define('#', Items.IRON_INGOT)
            .define('X', Items.REDSTONE)
            .pattern(" # ")
            .pattern("#X#")
            .pattern(" # ")
            .unlockedBy("has_redstone", has(Items.REDSTONE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Items.COOKIE, 8)
            .define('#', Items.WHEAT)
            .define('X', Items.COCOA_BEANS)
            .pattern("#X#")
            .unlockedBy("has_cocoa", has(Items.COCOA_BEANS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.CRAFTING_TABLE)
            .define('#', ItemTags.PLANKS)
            .pattern("##")
            .pattern("##")
            .unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
            .showNotification(false)
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.CROSSBOW)
            .define('~', Items.STRING)
            .define('#', Items.STICK)
            .define('&', Items.IRON_INGOT)
            .define('$', Blocks.TRIPWIRE_HOOK)
            .pattern("#&#")
            .pattern("~$~")
            .pattern(" # ")
            .unlockedBy("has_string", has(Items.STRING))
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .unlockedBy("has_tripwire_hook", has(Blocks.TRIPWIRE_HOOK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.LOOM)
            .define('#', ItemTags.PLANKS)
            .define('@', Items.STRING)
            .pattern("@@")
            .pattern("##")
            .unlockedBy("has_string", has(Items.STRING))
            .save(exporter);
        chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_RED_SANDSTONE, Ingredient.of(Blocks.RED_SANDSTONE_SLAB))
            .unlockedBy("has_red_sandstone", has(Blocks.RED_SANDSTONE))
            .unlockedBy("has_chiseled_red_sandstone", has(Blocks.CHISELED_RED_SANDSTONE))
            .unlockedBy("has_cut_red_sandstone", has(Blocks.CUT_RED_SANDSTONE))
            .save(exporter);
        chiseled(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE_SLAB);
        nineBlockStorageRecipesRecipesWithCustomUnpacking(
            exporter,
            RecipeCategory.MISC,
            Items.COPPER_INGOT,
            RecipeCategory.BUILDING_BLOCKS,
            Items.COPPER_BLOCK,
            getSimpleRecipeName(Items.COPPER_INGOT),
            getItemName(Items.COPPER_INGOT)
        );
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.COPPER_INGOT, 9)
            .requires(Blocks.WAXED_COPPER_BLOCK)
            .group(getItemName(Items.COPPER_INGOT))
            .unlockedBy(getHasName(Blocks.WAXED_COPPER_BLOCK), has(Blocks.WAXED_COPPER_BLOCK))
            .save(exporter, getConversionRecipeName(Items.COPPER_INGOT, Blocks.WAXED_COPPER_BLOCK));
        waxRecipes(exporter, FeatureFlagSet.of(FeatureFlags.VANILLA));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CYAN_DYE, 2)
            .requires(Items.BLUE_DYE)
            .requires(Items.GREEN_DYE)
            .group("cyan_dye")
            .unlockedBy("has_green_dye", has(Items.GREEN_DYE))
            .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.DARK_PRISMARINE)
            .define('S', Items.PRISMARINE_SHARD)
            .define('I', Items.BLACK_DYE)
            .pattern("SSS")
            .pattern("SIS")
            .pattern("SSS")
            .unlockedBy("has_prismarine_shard", has(Items.PRISMARINE_SHARD))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.DAYLIGHT_DETECTOR)
            .define('Q', Items.QUARTZ)
            .define('G', Blocks.GLASS)
            .define('W', Ingredient.of(ItemTags.WOODEN_SLABS))
            .pattern("GGG")
            .pattern("QQQ")
            .pattern("WWW")
            .unlockedBy("has_quartz", has(Items.QUARTZ))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICKS, 4)
            .define('S', Blocks.POLISHED_DEEPSLATE)
            .pattern("SS")
            .pattern("SS")
            .unlockedBy("has_polished_deepslate", has(Blocks.POLISHED_DEEPSLATE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, 4)
            .define('S', Blocks.DEEPSLATE_BRICKS)
            .pattern("SS")
            .pattern("SS")
            .unlockedBy("has_deepslate_bricks", has(Blocks.DEEPSLATE_BRICKS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Blocks.DETECTOR_RAIL, 6)
            .define('R', Items.REDSTONE)
            .define('#', Blocks.STONE_PRESSURE_PLATE)
            .define('X', Items.IRON_INGOT)
            .pattern("X X")
            .pattern("X#X")
            .pattern("XRX")
            .unlockedBy("has_rail", has(Blocks.RAIL))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.DIAMOND_AXE)
            .define('#', Items.STICK)
            .define('X', Items.DIAMOND)
            .pattern("XX")
            .pattern("X#")
            .pattern(" #")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(exporter);
        nineBlockStorageRecipes(exporter, RecipeCategory.MISC, Items.DIAMOND, RecipeCategory.BUILDING_BLOCKS, Items.DIAMOND_BLOCK);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.DIAMOND_BOOTS)
            .define('X', Items.DIAMOND)
            .pattern("X X")
            .pattern("X X")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.DIAMOND_CHESTPLATE)
            .define('X', Items.DIAMOND)
            .pattern("X X")
            .pattern("XXX")
            .pattern("XXX")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.DIAMOND_HELMET)
            .define('X', Items.DIAMOND)
            .pattern("XXX")
            .pattern("X X")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.DIAMOND_HOE)
            .define('#', Items.STICK)
            .define('X', Items.DIAMOND)
            .pattern("XX")
            .pattern(" #")
            .pattern(" #")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.DIAMOND_LEGGINGS)
            .define('X', Items.DIAMOND)
            .pattern("XXX")
            .pattern("X X")
            .pattern("X X")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.DIAMOND_PICKAXE)
            .define('#', Items.STICK)
            .define('X', Items.DIAMOND)
            .pattern("XXX")
            .pattern(" # ")
            .pattern(" # ")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.DIAMOND_SHOVEL)
            .define('#', Items.STICK)
            .define('X', Items.DIAMOND)
            .pattern("X")
            .pattern("#")
            .pattern("#")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.DIAMOND_SWORD)
            .define('#', Items.STICK)
            .define('X', Items.DIAMOND)
            .pattern("X")
            .pattern("X")
            .pattern("#")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.DIORITE, 2)
            .define('Q', Items.QUARTZ)
            .define('C', Blocks.COBBLESTONE)
            .pattern("CQ")
            .pattern("QC")
            .unlockedBy("has_quartz", has(Items.QUARTZ))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.DISPENSER)
            .define('R', Items.REDSTONE)
            .define('#', Blocks.COBBLESTONE)
            .define('X', Items.BOW)
            .pattern("###")
            .pattern("#X#")
            .pattern("#R#")
            .unlockedBy("has_bow", has(Items.BOW))
            .save(exporter);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DRIPSTONE_BLOCK, Items.POINTED_DRIPSTONE);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.DROPPER)
            .define('R', Items.REDSTONE)
            .define('#', Blocks.COBBLESTONE)
            .pattern("###")
            .pattern("# #")
            .pattern("#R#")
            .unlockedBy("has_redstone", has(Items.REDSTONE))
            .save(exporter);
        nineBlockStorageRecipes(exporter, RecipeCategory.MISC, Items.EMERALD, RecipeCategory.BUILDING_BLOCKS, Items.EMERALD_BLOCK);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.ENCHANTING_TABLE)
            .define('B', Items.BOOK)
            .define('#', Blocks.OBSIDIAN)
            .define('D', Items.DIAMOND)
            .pattern(" B ")
            .pattern("D#D")
            .pattern("###")
            .unlockedBy("has_obsidian", has(Blocks.OBSIDIAN))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.ENDER_CHEST)
            .define('#', Blocks.OBSIDIAN)
            .define('E', Items.ENDER_EYE)
            .pattern("###")
            .pattern("#E#")
            .pattern("###")
            .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.ENDER_EYE)
            .requires(Items.ENDER_PEARL)
            .requires(Items.BLAZE_POWDER)
            .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICKS, 4)
            .define('#', Blocks.END_STONE)
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_end_stone", has(Blocks.END_STONE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.END_CRYSTAL)
            .define('T', Items.GHAST_TEAR)
            .define('E', Items.ENDER_EYE)
            .define('G', Blocks.GLASS)
            .pattern("GGG")
            .pattern("GEG")
            .pattern("GTG")
            .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.END_ROD, 4)
            .define('#', Items.POPPED_CHORUS_FRUIT)
            .define('/', Items.BLAZE_ROD)
            .pattern("/")
            .pattern("#")
            .unlockedBy("has_chorus_fruit_popped", has(Items.POPPED_CHORUS_FRUIT))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BREWING, Items.FERMENTED_SPIDER_EYE)
            .requires(Items.SPIDER_EYE)
            .requires(Blocks.BROWN_MUSHROOM)
            .requires(Items.SUGAR)
            .unlockedBy("has_spider_eye", has(Items.SPIDER_EYE))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.FIRE_CHARGE, 3)
            .requires(Items.GUNPOWDER)
            .requires(Items.BLAZE_POWDER)
            .requires(Ingredient.of(Items.COAL, Items.CHARCOAL))
            .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.FIREWORK_ROCKET, 3)
            .requires(Items.GUNPOWDER)
            .requires(Items.PAPER)
            .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
            .save(exporter, "firework_rocket_simple");
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.FISHING_ROD)
            .define('#', Items.STICK)
            .define('X', Items.STRING)
            .pattern("  #")
            .pattern(" #X")
            .pattern("# X")
            .unlockedBy("has_string", has(Items.STRING))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, Items.FLINT_AND_STEEL)
            .requires(Items.IRON_INGOT)
            .requires(Items.FLINT)
            .unlockedBy("has_flint", has(Items.FLINT))
            .unlockedBy("has_obsidian", has(Blocks.OBSIDIAN))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.FLOWER_POT)
            .define('#', Items.BRICK)
            .pattern("# #")
            .pattern(" # ")
            .unlockedBy("has_brick", has(Items.BRICK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.FURNACE)
            .define('#', ItemTags.STONE_CRAFTING_MATERIALS)
            .pattern("###")
            .pattern("# #")
            .pattern("###")
            .unlockedBy("has_cobblestone", has(ItemTags.STONE_CRAFTING_MATERIALS))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, Items.FURNACE_MINECART)
            .requires(Blocks.FURNACE)
            .requires(Items.MINECART)
            .unlockedBy("has_minecart", has(Items.MINECART))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Items.GLASS_BOTTLE, 3)
            .define('#', Blocks.GLASS)
            .pattern("# #")
            .pattern(" # ")
            .unlockedBy("has_glass", has(Blocks.GLASS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.GLASS_PANE, 16)
            .define('#', Blocks.GLASS)
            .pattern("###")
            .pattern("###")
            .unlockedBy("has_glass", has(Blocks.GLASS))
            .save(exporter);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.GLOWSTONE, Items.GLOWSTONE_DUST);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, Items.GLOW_ITEM_FRAME)
            .requires(Items.ITEM_FRAME)
            .requires(Items.GLOW_INK_SAC)
            .unlockedBy("has_item_frame", has(Items.ITEM_FRAME))
            .unlockedBy("has_glow_ink_sac", has(Items.GLOW_INK_SAC))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Items.GOLDEN_APPLE)
            .define('#', Items.GOLD_INGOT)
            .define('X', Items.APPLE)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.GOLDEN_AXE)
            .define('#', Items.STICK)
            .define('X', Items.GOLD_INGOT)
            .pattern("XX")
            .pattern("X#")
            .pattern(" #")
            .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.GOLDEN_BOOTS)
            .define('X', Items.GOLD_INGOT)
            .pattern("X X")
            .pattern("X X")
            .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Items.GOLDEN_CARROT)
            .define('#', Items.GOLD_NUGGET)
            .define('X', Items.CARROT)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .unlockedBy("has_gold_nugget", has(Items.GOLD_NUGGET))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.GOLDEN_CHESTPLATE)
            .define('X', Items.GOLD_INGOT)
            .pattern("X X")
            .pattern("XXX")
            .pattern("XXX")
            .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.GOLDEN_HELMET)
            .define('X', Items.GOLD_INGOT)
            .pattern("XXX")
            .pattern("X X")
            .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.GOLDEN_HOE)
            .define('#', Items.STICK)
            .define('X', Items.GOLD_INGOT)
            .pattern("XX")
            .pattern(" #")
            .pattern(" #")
            .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.GOLDEN_LEGGINGS)
            .define('X', Items.GOLD_INGOT)
            .pattern("XXX")
            .pattern("X X")
            .pattern("X X")
            .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.GOLDEN_PICKAXE)
            .define('#', Items.STICK)
            .define('X', Items.GOLD_INGOT)
            .pattern("XXX")
            .pattern(" # ")
            .pattern(" # ")
            .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Blocks.POWERED_RAIL, 6)
            .define('R', Items.REDSTONE)
            .define('#', Items.STICK)
            .define('X', Items.GOLD_INGOT)
            .pattern("X X")
            .pattern("X#X")
            .pattern("XRX")
            .unlockedBy("has_rail", has(Blocks.RAIL))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.GOLDEN_SHOVEL)
            .define('#', Items.STICK)
            .define('X', Items.GOLD_INGOT)
            .pattern("X")
            .pattern("#")
            .pattern("#")
            .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.GOLDEN_SWORD)
            .define('#', Items.STICK)
            .define('X', Items.GOLD_INGOT)
            .pattern("X")
            .pattern("X")
            .pattern("#")
            .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
            .save(exporter);
        nineBlockStorageRecipesRecipesWithCustomUnpacking(
            exporter, RecipeCategory.MISC, Items.GOLD_INGOT, RecipeCategory.BUILDING_BLOCKS, Items.GOLD_BLOCK, "gold_ingot_from_gold_block", "gold_ingot"
        );
        nineBlockStorageRecipesWithCustomPacking(
            exporter, RecipeCategory.MISC, Items.GOLD_NUGGET, RecipeCategory.MISC, Items.GOLD_INGOT, "gold_ingot_from_nuggets", "gold_ingot"
        );
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.GRANITE)
            .requires(Blocks.DIORITE)
            .requires(Items.QUARTZ)
            .unlockedBy("has_quartz", has(Items.QUARTZ))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GRAY_DYE, 2)
            .requires(Items.BLACK_DYE)
            .requires(Items.WHITE_DYE)
            .unlockedBy("has_white_dye", has(Items.WHITE_DYE))
            .unlockedBy("has_black_dye", has(Items.BLACK_DYE))
            .save(exporter);
        threeByThreePacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.HAY_BLOCK, Items.WHEAT);
        pressurePlate(exporter, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_INGOT);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.HONEY_BOTTLE, 4)
            .requires(Items.HONEY_BLOCK)
            .requires(Items.GLASS_BOTTLE, 4)
            .unlockedBy("has_honey_block", has(Blocks.HONEY_BLOCK))
            .save(exporter);
        twoByTwoPacker(exporter, RecipeCategory.REDSTONE, Blocks.HONEY_BLOCK, Items.HONEY_BOTTLE);
        twoByTwoPacker(exporter, RecipeCategory.DECORATIONS, Blocks.HONEYCOMB_BLOCK, Items.HONEYCOMB);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.HOPPER)
            .define('C', Blocks.CHEST)
            .define('I', Items.IRON_INGOT)
            .pattern("I I")
            .pattern("ICI")
            .pattern(" I ")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, Items.HOPPER_MINECART)
            .requires(Blocks.HOPPER)
            .requires(Items.MINECART)
            .unlockedBy("has_minecart", has(Items.MINECART))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.IRON_AXE)
            .define('#', Items.STICK)
            .define('X', Items.IRON_INGOT)
            .pattern("XX")
            .pattern("X#")
            .pattern(" #")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.IRON_BARS, 16)
            .define('#', Items.IRON_INGOT)
            .pattern("###")
            .pattern("###")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.IRON_BOOTS)
            .define('X', Items.IRON_INGOT)
            .pattern("X X")
            .pattern("X X")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.IRON_CHESTPLATE)
            .define('X', Items.IRON_INGOT)
            .pattern("X X")
            .pattern("XXX")
            .pattern("XXX")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        doorBuilder(Blocks.IRON_DOOR, Ingredient.of(Items.IRON_INGOT)).unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT)).save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.IRON_HELMET)
            .define('X', Items.IRON_INGOT)
            .pattern("XXX")
            .pattern("X X")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.IRON_HOE)
            .define('#', Items.STICK)
            .define('X', Items.IRON_INGOT)
            .pattern("XX")
            .pattern(" #")
            .pattern(" #")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        nineBlockStorageRecipesRecipesWithCustomUnpacking(
            exporter, RecipeCategory.MISC, Items.IRON_INGOT, RecipeCategory.BUILDING_BLOCKS, Items.IRON_BLOCK, "iron_ingot_from_iron_block", "iron_ingot"
        );
        nineBlockStorageRecipesWithCustomPacking(
            exporter, RecipeCategory.MISC, Items.IRON_NUGGET, RecipeCategory.MISC, Items.IRON_INGOT, "iron_ingot_from_nuggets", "iron_ingot"
        );
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.IRON_LEGGINGS)
            .define('X', Items.IRON_INGOT)
            .pattern("XXX")
            .pattern("X X")
            .pattern("X X")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.IRON_PICKAXE)
            .define('#', Items.STICK)
            .define('X', Items.IRON_INGOT)
            .pattern("XXX")
            .pattern(" # ")
            .pattern(" # ")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.IRON_SHOVEL)
            .define('#', Items.STICK)
            .define('X', Items.IRON_INGOT)
            .pattern("X")
            .pattern("#")
            .pattern("#")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.IRON_SWORD)
            .define('#', Items.STICK)
            .define('X', Items.IRON_INGOT)
            .pattern("X")
            .pattern("X")
            .pattern("#")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        twoByTwoPacker(exporter, RecipeCategory.REDSTONE, Blocks.IRON_TRAPDOOR, Items.IRON_INGOT);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.ITEM_FRAME)
            .define('#', Items.STICK)
            .define('X', Items.LEATHER)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .unlockedBy("has_leather", has(Items.LEATHER))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.JUKEBOX)
            .define('#', ItemTags.PLANKS)
            .define('X', Items.DIAMOND)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.LADDER, 3)
            .define('#', Items.STICK)
            .pattern("# #")
            .pattern("###")
            .pattern("# #")
            .unlockedBy("has_stick", has(Items.STICK))
            .save(exporter);
        nineBlockStorageRecipes(exporter, RecipeCategory.MISC, Items.LAPIS_LAZULI, RecipeCategory.BUILDING_BLOCKS, Items.LAPIS_BLOCK);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.LEAD, 2)
            .define('~', Items.STRING)
            .define('O', Items.SLIME_BALL)
            .pattern("~~ ")
            .pattern("~O ")
            .pattern("  ~")
            .unlockedBy("has_slime_ball", has(Items.SLIME_BALL))
            .save(exporter);
        twoByTwoPacker(exporter, RecipeCategory.MISC, Items.LEATHER, Items.RABBIT_HIDE);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.LEATHER_BOOTS)
            .define('X', Items.LEATHER)
            .pattern("X X")
            .pattern("X X")
            .unlockedBy("has_leather", has(Items.LEATHER))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.LEATHER_CHESTPLATE)
            .define('X', Items.LEATHER)
            .pattern("X X")
            .pattern("XXX")
            .pattern("XXX")
            .unlockedBy("has_leather", has(Items.LEATHER))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.LEATHER_HELMET)
            .define('X', Items.LEATHER)
            .pattern("XXX")
            .pattern("X X")
            .unlockedBy("has_leather", has(Items.LEATHER))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.LEATHER_LEGGINGS)
            .define('X', Items.LEATHER)
            .pattern("XXX")
            .pattern("X X")
            .pattern("X X")
            .unlockedBy("has_leather", has(Items.LEATHER))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.LEATHER_HORSE_ARMOR)
            .define('X', Items.LEATHER)
            .pattern("X X")
            .pattern("XXX")
            .pattern("X X")
            .unlockedBy("has_leather", has(Items.LEATHER))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.LECTERN)
            .define('S', ItemTags.WOODEN_SLABS)
            .define('B', Blocks.BOOKSHELF)
            .pattern("SSS")
            .pattern(" B ")
            .pattern(" S ")
            .unlockedBy("has_book", has(Items.BOOK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.LEVER)
            .define('#', Blocks.COBBLESTONE)
            .define('X', Items.STICK)
            .pattern("X")
            .pattern("#")
            .unlockedBy("has_cobblestone", has(Blocks.COBBLESTONE))
            .save(exporter);
        oneToOneConversionRecipe(exporter, Items.LIGHT_BLUE_DYE, Blocks.BLUE_ORCHID, "light_blue_dye");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE, 2)
            .requires(Items.BLUE_DYE)
            .requires(Items.WHITE_DYE)
            .group("light_blue_dye")
            .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
            .unlockedBy("has_white_dye", has(Items.WHITE_DYE))
            .save(exporter, "light_blue_dye_from_blue_white_dye");
        oneToOneConversionRecipe(exporter, Items.LIGHT_GRAY_DYE, Blocks.AZURE_BLUET, "light_gray_dye");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LIGHT_GRAY_DYE, 2)
            .requires(Items.GRAY_DYE)
            .requires(Items.WHITE_DYE)
            .group("light_gray_dye")
            .unlockedBy("has_gray_dye", has(Items.GRAY_DYE))
            .unlockedBy("has_white_dye", has(Items.WHITE_DYE))
            .save(exporter, "light_gray_dye_from_gray_white_dye");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LIGHT_GRAY_DYE, 3)
            .requires(Items.BLACK_DYE)
            .requires(Items.WHITE_DYE, 2)
            .group("light_gray_dye")
            .unlockedBy("has_white_dye", has(Items.WHITE_DYE))
            .unlockedBy("has_black_dye", has(Items.BLACK_DYE))
            .save(exporter, "light_gray_dye_from_black_white_dye");
        oneToOneConversionRecipe(exporter, Items.LIGHT_GRAY_DYE, Blocks.OXEYE_DAISY, "light_gray_dye");
        oneToOneConversionRecipe(exporter, Items.LIGHT_GRAY_DYE, Blocks.WHITE_TULIP, "light_gray_dye");
        pressurePlate(exporter, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.LIGHTNING_ROD)
            .define('#', Items.COPPER_INGOT)
            .pattern("#")
            .pattern("#")
            .pattern("#")
            .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LIME_DYE, 2)
            .requires(Items.GREEN_DYE)
            .requires(Items.WHITE_DYE)
            .unlockedBy("has_green_dye", has(Items.GREEN_DYE))
            .unlockedBy("has_white_dye", has(Items.WHITE_DYE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.JACK_O_LANTERN)
            .define('A', Blocks.CARVED_PUMPKIN)
            .define('B', Blocks.TORCH)
            .pattern("A")
            .pattern("B")
            .unlockedBy("has_carved_pumpkin", has(Blocks.CARVED_PUMPKIN))
            .save(exporter);
        oneToOneConversionRecipe(exporter, Items.MAGENTA_DYE, Blocks.ALLIUM, "magenta_dye");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.MAGENTA_DYE, 4)
            .requires(Items.BLUE_DYE)
            .requires(Items.RED_DYE, 2)
            .requires(Items.WHITE_DYE)
            .group("magenta_dye")
            .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
            .unlockedBy("has_rose_red", has(Items.RED_DYE))
            .unlockedBy("has_white_dye", has(Items.WHITE_DYE))
            .save(exporter, "magenta_dye_from_blue_red_white_dye");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.MAGENTA_DYE, 3)
            .requires(Items.BLUE_DYE)
            .requires(Items.RED_DYE)
            .requires(Items.PINK_DYE)
            .group("magenta_dye")
            .unlockedBy("has_pink_dye", has(Items.PINK_DYE))
            .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
            .unlockedBy("has_red_dye", has(Items.RED_DYE))
            .save(exporter, "magenta_dye_from_blue_red_pink");
        oneToOneConversionRecipe(exporter, Items.MAGENTA_DYE, Blocks.LILAC, "magenta_dye", 2);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.MAGENTA_DYE, 2)
            .requires(Items.PURPLE_DYE)
            .requires(Items.PINK_DYE)
            .group("magenta_dye")
            .unlockedBy("has_pink_dye", has(Items.PINK_DYE))
            .unlockedBy("has_purple_dye", has(Items.PURPLE_DYE))
            .save(exporter, "magenta_dye_from_purple_and_pink");
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MAGMA_BLOCK, Items.MAGMA_CREAM);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BREWING, Items.MAGMA_CREAM)
            .requires(Items.BLAZE_POWDER)
            .requires(Items.SLIME_BALL)
            .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.MAP)
            .define('#', Items.PAPER)
            .define('X', Items.COMPASS)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .unlockedBy("has_compass", has(Items.COMPASS))
            .save(exporter);
        threeByThreePacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MELON, Items.MELON_SLICE, "has_melon");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.MELON_SEEDS)
            .requires(Items.MELON_SLICE)
            .unlockedBy("has_melon", has(Items.MELON_SLICE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Items.MINECART)
            .define('#', Items.IRON_INGOT)
            .pattern("# #")
            .pattern("###")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE)
            .requires(Blocks.COBBLESTONE)
            .requires(Blocks.VINE)
            .group("mossy_cobblestone")
            .unlockedBy("has_vine", has(Blocks.VINE))
            .save(exporter, getConversionRecipeName(Blocks.MOSSY_COBBLESTONE, Blocks.VINE));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICKS)
            .requires(Blocks.STONE_BRICKS)
            .requires(Blocks.VINE)
            .group("mossy_stone_bricks")
            .unlockedBy("has_vine", has(Blocks.VINE))
            .save(exporter, getConversionRecipeName(Blocks.MOSSY_STONE_BRICKS, Blocks.VINE));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE)
            .requires(Blocks.COBBLESTONE)
            .requires(Blocks.MOSS_BLOCK)
            .group("mossy_cobblestone")
            .unlockedBy("has_moss_block", has(Blocks.MOSS_BLOCK))
            .save(exporter, getConversionRecipeName(Blocks.MOSSY_COBBLESTONE, Blocks.MOSS_BLOCK));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICKS)
            .requires(Blocks.STONE_BRICKS)
            .requires(Blocks.MOSS_BLOCK)
            .group("mossy_stone_bricks")
            .unlockedBy("has_moss_block", has(Blocks.MOSS_BLOCK))
            .save(exporter, getConversionRecipeName(Blocks.MOSSY_STONE_BRICKS, Blocks.MOSS_BLOCK));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.MUSHROOM_STEW)
            .requires(Blocks.BROWN_MUSHROOM)
            .requires(Blocks.RED_MUSHROOM)
            .requires(Items.BOWL)
            .unlockedBy("has_mushroom_stew", has(Items.MUSHROOM_STEW))
            .unlockedBy("has_bowl", has(Items.BOWL))
            .unlockedBy("has_brown_mushroom", has(Blocks.BROWN_MUSHROOM))
            .unlockedBy("has_red_mushroom", has(Blocks.RED_MUSHROOM))
            .save(exporter);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_BRICKS, Items.NETHER_BRICK);
        threeByThreePacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_WART_BLOCK, Items.NETHER_WART);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.NOTE_BLOCK)
            .define('#', ItemTags.PLANKS)
            .define('X', Items.REDSTONE)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .unlockedBy("has_redstone", has(Items.REDSTONE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.OBSERVER)
            .define('Q', Items.QUARTZ)
            .define('R', Items.REDSTONE)
            .define('#', Blocks.COBBLESTONE)
            .pattern("###")
            .pattern("RRQ")
            .pattern("###")
            .unlockedBy("has_quartz", has(Items.QUARTZ))
            .save(exporter);
        oneToOneConversionRecipe(exporter, Items.ORANGE_DYE, Blocks.ORANGE_TULIP, "orange_dye");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.ORANGE_DYE, 2)
            .requires(Items.RED_DYE)
            .requires(Items.YELLOW_DYE)
            .group("orange_dye")
            .unlockedBy("has_red_dye", has(Items.RED_DYE))
            .unlockedBy("has_yellow_dye", has(Items.YELLOW_DYE))
            .save(exporter, "orange_dye_from_red_yellow");
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.PAINTING)
            .define('#', Items.STICK)
            .define('X', Ingredient.of(ItemTags.WOOL))
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .unlockedBy("has_wool", has(ItemTags.WOOL))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PAPER, 3)
            .define('#', Blocks.SUGAR_CANE)
            .pattern("###")
            .unlockedBy("has_reeds", has(Blocks.SUGAR_CANE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_PILLAR, 2)
            .define('#', Blocks.QUARTZ_BLOCK)
            .pattern("#")
            .pattern("#")
            .unlockedBy("has_chiseled_quartz_block", has(Blocks.CHISELED_QUARTZ_BLOCK))
            .unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK))
            .unlockedBy("has_quartz_pillar", has(Blocks.QUARTZ_PILLAR))
            .save(exporter);
        threeByThreePacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PACKED_ICE, Blocks.ICE);
        oneToOneConversionRecipe(exporter, Items.PINK_DYE, Blocks.PEONY, "pink_dye", 2);
        oneToOneConversionRecipe(exporter, Items.PINK_DYE, Blocks.PINK_TULIP, "pink_dye");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PINK_DYE, 2)
            .requires(Items.RED_DYE)
            .requires(Items.WHITE_DYE)
            .group("pink_dye")
            .unlockedBy("has_white_dye", has(Items.WHITE_DYE))
            .unlockedBy("has_red_dye", has(Items.RED_DYE))
            .save(exporter, "pink_dye_from_red_white_dye");
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.PISTON)
            .define('R', Items.REDSTONE)
            .define('#', Blocks.COBBLESTONE)
            .define('T', ItemTags.PLANKS)
            .define('X', Items.IRON_INGOT)
            .pattern("TTT")
            .pattern("#X#")
            .pattern("#R#")
            .unlockedBy("has_redstone", has(Items.REDSTONE))
            .save(exporter);
        polished(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BASALT, Blocks.BASALT);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE, Items.PRISMARINE_SHARD);
        threeByThreePacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_BRICKS, Items.PRISMARINE_SHARD);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.PUMPKIN_PIE)
            .requires(Blocks.PUMPKIN)
            .requires(Items.SUGAR)
            .requires(Items.EGG)
            .unlockedBy("has_carved_pumpkin", has(Blocks.CARVED_PUMPKIN))
            .unlockedBy("has_pumpkin", has(Blocks.PUMPKIN))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PUMPKIN_SEEDS, 4)
            .requires(Blocks.PUMPKIN)
            .unlockedBy("has_pumpkin", has(Blocks.PUMPKIN))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PURPLE_DYE, 2)
            .requires(Items.BLUE_DYE)
            .requires(Items.RED_DYE)
            .unlockedBy("has_blue_dye", has(Items.BLUE_DYE))
            .unlockedBy("has_red_dye", has(Items.RED_DYE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.SHULKER_BOX)
            .define('#', Blocks.CHEST)
            .define('-', Items.SHULKER_SHELL)
            .pattern("-")
            .pattern("#")
            .pattern("-")
            .unlockedBy("has_shulker_shell", has(Items.SHULKER_SHELL))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_BLOCK, 4)
            .define('F', Items.POPPED_CHORUS_FRUIT)
            .pattern("FF")
            .pattern("FF")
            .unlockedBy("has_chorus_fruit_popped", has(Items.POPPED_CHORUS_FRUIT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_PILLAR)
            .define('#', Blocks.PURPUR_SLAB)
            .pattern("#")
            .pattern("#")
            .unlockedBy("has_purpur_block", has(Blocks.PURPUR_BLOCK))
            .save(exporter);
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_SLAB, Ingredient.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR))
            .unlockedBy("has_purpur_block", has(Blocks.PURPUR_BLOCK))
            .save(exporter);
        stairBuilder(Blocks.PURPUR_STAIRS, Ingredient.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR))
            .unlockedBy("has_purpur_block", has(Blocks.PURPUR_BLOCK))
            .save(exporter);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_BLOCK, Items.QUARTZ);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_BRICKS, 4)
            .define('#', Blocks.QUARTZ_BLOCK)
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK))
            .save(exporter);
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_SLAB, Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR))
            .unlockedBy("has_chiseled_quartz_block", has(Blocks.CHISELED_QUARTZ_BLOCK))
            .unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK))
            .unlockedBy("has_quartz_pillar", has(Blocks.QUARTZ_PILLAR))
            .save(exporter);
        stairBuilder(Blocks.QUARTZ_STAIRS, Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR))
            .unlockedBy("has_chiseled_quartz_block", has(Blocks.CHISELED_QUARTZ_BLOCK))
            .unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK))
            .unlockedBy("has_quartz_pillar", has(Blocks.QUARTZ_PILLAR))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.RABBIT_STEW)
            .requires(Items.BAKED_POTATO)
            .requires(Items.COOKED_RABBIT)
            .requires(Items.BOWL)
            .requires(Items.CARROT)
            .requires(Blocks.BROWN_MUSHROOM)
            .group("rabbit_stew")
            .unlockedBy("has_cooked_rabbit", has(Items.COOKED_RABBIT))
            .save(exporter, getConversionRecipeName(Items.RABBIT_STEW, Items.BROWN_MUSHROOM));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.RABBIT_STEW)
            .requires(Items.BAKED_POTATO)
            .requires(Items.COOKED_RABBIT)
            .requires(Items.BOWL)
            .requires(Items.CARROT)
            .requires(Blocks.RED_MUSHROOM)
            .group("rabbit_stew")
            .unlockedBy("has_cooked_rabbit", has(Items.COOKED_RABBIT))
            .save(exporter, getConversionRecipeName(Items.RABBIT_STEW, Items.RED_MUSHROOM));
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, Blocks.RAIL, 16)
            .define('#', Items.STICK)
            .define('X', Items.IRON_INGOT)
            .pattern("X X")
            .pattern("X#X")
            .pattern("X X")
            .unlockedBy("has_minecart", has(Items.MINECART))
            .save(exporter);
        nineBlockStorageRecipes(exporter, RecipeCategory.REDSTONE, Items.REDSTONE, RecipeCategory.REDSTONE, Items.REDSTONE_BLOCK);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.REDSTONE_LAMP)
            .define('R', Items.REDSTONE)
            .define('G', Blocks.GLOWSTONE)
            .pattern(" R ")
            .pattern("RGR")
            .pattern(" R ")
            .unlockedBy("has_glowstone", has(Blocks.GLOWSTONE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.REDSTONE_TORCH)
            .define('#', Items.STICK)
            .define('X', Items.REDSTONE)
            .pattern("X")
            .pattern("#")
            .unlockedBy("has_redstone", has(Items.REDSTONE))
            .save(exporter);
        oneToOneConversionRecipe(exporter, Items.RED_DYE, Items.BEETROOT, "red_dye");
        oneToOneConversionRecipe(exporter, Items.RED_DYE, Blocks.POPPY, "red_dye");
        oneToOneConversionRecipe(exporter, Items.RED_DYE, Blocks.ROSE_BUSH, "red_dye", 2);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.RED_DYE)
            .requires(Blocks.RED_TULIP)
            .group("red_dye")
            .unlockedBy("has_red_flower", has(Blocks.RED_TULIP))
            .save(exporter, "red_dye_from_tulip");
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_NETHER_BRICKS)
            .define('W', Items.NETHER_WART)
            .define('N', Items.NETHER_BRICK)
            .pattern("NW")
            .pattern("WN")
            .unlockedBy("has_nether_wart", has(Items.NETHER_WART))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE)
            .define('#', Blocks.RED_SAND)
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_sand", has(Blocks.RED_SAND))
            .save(exporter);
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE_SLAB, Ingredient.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE))
            .unlockedBy("has_red_sandstone", has(Blocks.RED_SANDSTONE))
            .unlockedBy("has_chiseled_red_sandstone", has(Blocks.CHISELED_RED_SANDSTONE))
            .save(exporter);
        stairBuilder(Blocks.RED_SANDSTONE_STAIRS, Ingredient.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE))
            .unlockedBy("has_red_sandstone", has(Blocks.RED_SANDSTONE))
            .unlockedBy("has_chiseled_red_sandstone", has(Blocks.CHISELED_RED_SANDSTONE))
            .unlockedBy("has_cut_red_sandstone", has(Blocks.CUT_RED_SANDSTONE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.REPEATER)
            .define('#', Blocks.REDSTONE_TORCH)
            .define('X', Items.REDSTONE)
            .define('I', Blocks.STONE)
            .pattern("#X#")
            .pattern("III")
            .unlockedBy("has_redstone_torch", has(Blocks.REDSTONE_TORCH))
            .save(exporter);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE, Blocks.SAND);
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE_SLAB, Ingredient.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE))
            .unlockedBy("has_sandstone", has(Blocks.SANDSTONE))
            .unlockedBy("has_chiseled_sandstone", has(Blocks.CHISELED_SANDSTONE))
            .save(exporter);
        stairBuilder(Blocks.SANDSTONE_STAIRS, Ingredient.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE))
            .unlockedBy("has_sandstone", has(Blocks.SANDSTONE))
            .unlockedBy("has_chiseled_sandstone", has(Blocks.CHISELED_SANDSTONE))
            .unlockedBy("has_cut_sandstone", has(Blocks.CUT_SANDSTONE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.SEA_LANTERN)
            .define('S', Items.PRISMARINE_SHARD)
            .define('C', Items.PRISMARINE_CRYSTALS)
            .pattern("SCS")
            .pattern("CCC")
            .pattern("SCS")
            .unlockedBy("has_prismarine_crystals", has(Items.PRISMARINE_CRYSTALS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.SHEARS)
            .define('#', Items.IRON_INGOT)
            .pattern(" #")
            .pattern("# ")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.SHIELD)
            .define('W', ItemTags.PLANKS)
            .define('o', Items.IRON_INGOT)
            .pattern("WoW")
            .pattern("WWW")
            .pattern(" W ")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        nineBlockStorageRecipes(exporter, RecipeCategory.MISC, Items.SLIME_BALL, RecipeCategory.REDSTONE, Items.SLIME_BLOCK);
        cut(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
        cut(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SNOW_BLOCK, Items.SNOWBALL);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.SNOW, 6)
            .define('#', Blocks.SNOW_BLOCK)
            .pattern("###")
            .unlockedBy("has_snowball", has(Items.SNOWBALL))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.SOUL_CAMPFIRE)
            .define('L', ItemTags.LOGS)
            .define('S', Items.STICK)
            .define('#', ItemTags.SOUL_FIRE_BASE_BLOCKS)
            .pattern(" S ")
            .pattern("S#S")
            .pattern("LLL")
            .unlockedBy("has_soul_sand", has(ItemTags.SOUL_FIRE_BASE_BLOCKS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Items.GLISTERING_MELON_SLICE)
            .define('#', Items.GOLD_NUGGET)
            .define('X', Items.MELON_SLICE)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .unlockedBy("has_melon", has(Items.MELON_SLICE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.SPECTRAL_ARROW, 2)
            .define('#', Items.GLOWSTONE_DUST)
            .define('X', Items.ARROW)
            .pattern(" # ")
            .pattern("#X#")
            .pattern(" # ")
            .unlockedBy("has_glowstone_dust", has(Items.GLOWSTONE_DUST))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.SPYGLASS)
            .define('#', Items.AMETHYST_SHARD)
            .define('X', Items.COPPER_INGOT)
            .pattern(" # ")
            .pattern(" X ")
            .pattern(" X ")
            .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.STICK, 4)
            .define('#', ItemTags.PLANKS)
            .pattern("#")
            .pattern("#")
            .group("sticks")
            .unlockedBy("has_planks", has(ItemTags.PLANKS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.STICK, 1)
            .define('#', Blocks.BAMBOO)
            .pattern("#")
            .pattern("#")
            .group("sticks")
            .unlockedBy("has_bamboo", has(Blocks.BAMBOO))
            .save(exporter, "stick_from_bamboo_item");
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.STICKY_PISTON)
            .define('P', Blocks.PISTON)
            .define('S', Items.SLIME_BALL)
            .pattern("S")
            .pattern("P")
            .unlockedBy("has_slime_ball", has(Items.SLIME_BALL))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICKS, 4)
            .define('#', Blocks.STONE)
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_stone", has(Blocks.STONE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.STONE_AXE)
            .define('#', Items.STICK)
            .define('X', ItemTags.STONE_TOOL_MATERIALS)
            .pattern("XX")
            .pattern("X#")
            .pattern(" #")
            .unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS))
            .save(exporter);
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_SLAB, Ingredient.of(Blocks.STONE_BRICKS))
            .unlockedBy("has_stone_bricks", has(ItemTags.STONE_BRICKS))
            .save(exporter);
        stairBuilder(Blocks.STONE_BRICK_STAIRS, Ingredient.of(Blocks.STONE_BRICKS)).unlockedBy("has_stone_bricks", has(ItemTags.STONE_BRICKS)).save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.STONE_HOE)
            .define('#', Items.STICK)
            .define('X', ItemTags.STONE_TOOL_MATERIALS)
            .pattern("XX")
            .pattern(" #")
            .pattern(" #")
            .unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.STONE_PICKAXE)
            .define('#', Items.STICK)
            .define('X', ItemTags.STONE_TOOL_MATERIALS)
            .pattern("XXX")
            .pattern(" # ")
            .pattern(" # ")
            .unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.STONE_SHOVEL)
            .define('#', Items.STICK)
            .define('X', ItemTags.STONE_TOOL_MATERIALS)
            .pattern("X")
            .pattern("#")
            .pattern("#")
            .unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS))
            .save(exporter);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.STONE_SWORD)
            .define('#', Items.STICK)
            .define('X', ItemTags.STONE_TOOL_MATERIALS)
            .pattern("X")
            .pattern("X")
            .pattern("#")
            .unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.WHITE_WOOL)
            .define('#', Items.STRING)
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_string", has(Items.STRING))
            .save(exporter, getConversionRecipeName(Blocks.WHITE_WOOL, Items.STRING));
        oneToOneConversionRecipe(exporter, Items.SUGAR, Blocks.SUGAR_CANE, "sugar");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SUGAR, 3)
            .requires(Items.HONEY_BOTTLE)
            .group("sugar")
            .unlockedBy("has_honey_bottle", has(Items.HONEY_BOTTLE))
            .save(exporter, getConversionRecipeName(Items.SUGAR, Items.HONEY_BOTTLE));
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.TARGET)
            .define('H', Items.HAY_BLOCK)
            .define('R', Items.REDSTONE)
            .pattern(" R ")
            .pattern("RHR")
            .pattern(" R ")
            .unlockedBy("has_redstone", has(Items.REDSTONE))
            .unlockedBy("has_hay_block", has(Blocks.HAY_BLOCK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.TNT)
            .define('#', Ingredient.of(Blocks.SAND, Blocks.RED_SAND))
            .define('X', Items.GUNPOWDER)
            .pattern("X#X")
            .pattern("#X#")
            .pattern("X#X")
            .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, Items.TNT_MINECART)
            .requires(Blocks.TNT)
            .requires(Items.MINECART)
            .unlockedBy("has_minecart", has(Items.MINECART))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.TORCH, 4)
            .define('#', Items.STICK)
            .define('X', Ingredient.of(Items.COAL, Items.CHARCOAL))
            .pattern("X")
            .pattern("#")
            .unlockedBy("has_stone_pickaxe", has(Items.STONE_PICKAXE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.SOUL_TORCH, 4)
            .define('X', Ingredient.of(Items.COAL, Items.CHARCOAL))
            .define('#', Items.STICK)
            .define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
            .pattern("X")
            .pattern("#")
            .pattern("S")
            .unlockedBy("has_soul_sand", has(ItemTags.SOUL_FIRE_BASE_BLOCKS))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.LANTERN)
            .define('#', Items.TORCH)
            .define('X', Items.IRON_NUGGET)
            .pattern("XXX")
            .pattern("X#X")
            .pattern("XXX")
            .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.SOUL_LANTERN)
            .define('#', Items.SOUL_TORCH)
            .define('X', Items.IRON_NUGGET)
            .pattern("XXX")
            .pattern("X#X")
            .pattern("XXX")
            .unlockedBy("has_soul_torch", has(Items.SOUL_TORCH))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, Blocks.TRAPPED_CHEST)
            .requires(Blocks.CHEST)
            .requires(Blocks.TRIPWIRE_HOOK)
            .unlockedBy("has_tripwire_hook", has(Blocks.TRIPWIRE_HOOK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.TRIPWIRE_HOOK, 2)
            .define('#', ItemTags.PLANKS)
            .define('S', Items.STICK)
            .define('I', Items.IRON_INGOT)
            .pattern("I")
            .pattern("S")
            .pattern("#")
            .unlockedBy("has_string", has(Items.STRING))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.TURTLE_HELMET)
            .define('X', Items.SCUTE)
            .pattern("XXX")
            .pattern("X X")
            .unlockedBy("has_scute", has(Items.SCUTE))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.WHEAT, 9)
            .requires(Blocks.HAY_BLOCK)
            .unlockedBy("has_hay_block", has(Blocks.HAY_BLOCK))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.WHITE_DYE)
            .requires(Items.BONE_MEAL)
            .group("white_dye")
            .unlockedBy("has_bone_meal", has(Items.BONE_MEAL))
            .save(exporter);
        oneToOneConversionRecipe(exporter, Items.WHITE_DYE, Blocks.LILY_OF_THE_VALLEY, "white_dye");
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.WOODEN_AXE)
            .define('#', Items.STICK)
            .define('X', ItemTags.PLANKS)
            .pattern("XX")
            .pattern("X#")
            .pattern(" #")
            .unlockedBy("has_stick", has(Items.STICK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.WOODEN_HOE)
            .define('#', Items.STICK)
            .define('X', ItemTags.PLANKS)
            .pattern("XX")
            .pattern(" #")
            .pattern(" #")
            .unlockedBy("has_stick", has(Items.STICK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.WOODEN_PICKAXE)
            .define('#', Items.STICK)
            .define('X', ItemTags.PLANKS)
            .pattern("XXX")
            .pattern(" # ")
            .pattern(" # ")
            .unlockedBy("has_stick", has(Items.STICK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.WOODEN_SHOVEL)
            .define('#', Items.STICK)
            .define('X', ItemTags.PLANKS)
            .pattern("X")
            .pattern("#")
            .pattern("#")
            .unlockedBy("has_stick", has(Items.STICK))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.WOODEN_SWORD)
            .define('#', Items.STICK)
            .define('X', ItemTags.PLANKS)
            .pattern("X")
            .pattern("X")
            .pattern("#")
            .unlockedBy("has_stick", has(Items.STICK))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.WRITABLE_BOOK)
            .requires(Items.BOOK)
            .requires(Items.INK_SAC)
            .requires(Items.FEATHER)
            .unlockedBy("has_book", has(Items.BOOK))
            .save(exporter);
        oneToOneConversionRecipe(exporter, Items.YELLOW_DYE, Blocks.DANDELION, "yellow_dye");
        oneToOneConversionRecipe(exporter, Items.YELLOW_DYE, Blocks.SUNFLOWER, "yellow_dye", 2);
        nineBlockStorageRecipes(exporter, RecipeCategory.FOOD, Items.DRIED_KELP, RecipeCategory.BUILDING_BLOCKS, Items.DRIED_KELP_BLOCK);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.CONDUIT)
            .define('#', Items.NAUTILUS_SHELL)
            .define('X', Items.HEART_OF_THE_SEA)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .unlockedBy("has_nautilus_core", has(Items.HEART_OF_THE_SEA))
            .unlockedBy("has_nautilus_shell", has(Items.NAUTILUS_SHELL))
            .save(exporter);
        wall(exporter, RecipeCategory.DECORATIONS, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
        wall(exporter, RecipeCategory.DECORATIONS, Blocks.STONE_BRICK_WALL, Blocks.STONE_BRICKS);
        wall(exporter, RecipeCategory.DECORATIONS, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CREEPER_BANNER_PATTERN)
            .requires(Items.PAPER)
            .requires(Items.CREEPER_HEAD)
            .unlockedBy("has_creeper_head", has(Items.CREEPER_HEAD))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SKULL_BANNER_PATTERN)
            .requires(Items.PAPER)
            .requires(Items.WITHER_SKELETON_SKULL)
            .unlockedBy("has_wither_skeleton_skull", has(Items.WITHER_SKELETON_SKULL))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.FLOWER_BANNER_PATTERN)
            .requires(Items.PAPER)
            .requires(Blocks.OXEYE_DAISY)
            .unlockedBy("has_oxeye_daisy", has(Blocks.OXEYE_DAISY))
            .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.MOJANG_BANNER_PATTERN)
            .requires(Items.PAPER)
            .requires(Items.ENCHANTED_GOLDEN_APPLE)
            .unlockedBy("has_enchanted_golden_apple", has(Items.ENCHANTED_GOLDEN_APPLE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.SCAFFOLDING, 6)
            .define('~', Items.STRING)
            .define('I', Blocks.BAMBOO)
            .pattern("I~I")
            .pattern("I I")
            .pattern("I I")
            .unlockedBy("has_bamboo", has(Blocks.BAMBOO))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.GRINDSTONE)
            .define('I', Items.STICK)
            .define('-', Blocks.STONE_SLAB)
            .define('#', ItemTags.PLANKS)
            .pattern("I-I")
            .pattern("# #")
            .unlockedBy("has_stone_slab", has(Blocks.STONE_SLAB))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.BLAST_FURNACE)
            .define('#', Blocks.SMOOTH_STONE)
            .define('X', Blocks.FURNACE)
            .define('I', Items.IRON_INGOT)
            .pattern("III")
            .pattern("IXI")
            .pattern("###")
            .unlockedBy("has_smooth_stone", has(Blocks.SMOOTH_STONE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.SMOKER)
            .define('#', ItemTags.LOGS)
            .define('X', Blocks.FURNACE)
            .pattern(" # ")
            .pattern("#X#")
            .pattern(" # ")
            .unlockedBy("has_furnace", has(Blocks.FURNACE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.CARTOGRAPHY_TABLE)
            .define('#', ItemTags.PLANKS)
            .define('@', Items.PAPER)
            .pattern("@@")
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_paper", has(Items.PAPER))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.SMITHING_TABLE)
            .define('#', ItemTags.PLANKS)
            .define('@', Items.IRON_INGOT)
            .pattern("@@")
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.FLETCHING_TABLE)
            .define('#', ItemTags.PLANKS)
            .define('@', Items.FLINT)
            .pattern("@@")
            .pattern("##")
            .pattern("##")
            .unlockedBy("has_flint", has(Items.FLINT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.STONECUTTER)
            .define('I', Items.IRON_INGOT)
            .define('#', Blocks.STONE)
            .pattern(" I ")
            .pattern("###")
            .unlockedBy("has_stone", has(Blocks.STONE))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.LODESTONE)
            .define('S', Items.CHISELED_STONE_BRICKS)
            .define('#', Items.NETHERITE_INGOT)
            .pattern("SSS")
            .pattern("S#S")
            .pattern("SSS")
            .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
            .save(exporter);
        nineBlockStorageRecipesRecipesWithCustomUnpacking(
            exporter,
            RecipeCategory.MISC,
            Items.NETHERITE_INGOT,
            RecipeCategory.BUILDING_BLOCKS,
            Items.NETHERITE_BLOCK,
            "netherite_ingot_from_netherite_block",
            "netherite_ingot"
        );
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.NETHERITE_INGOT)
            .requires(Items.NETHERITE_SCRAP, 4)
            .requires(Items.GOLD_INGOT, 4)
            .group("netherite_ingot")
            .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.RESPAWN_ANCHOR)
            .define('O', Blocks.CRYING_OBSIDIAN)
            .define('G', Blocks.GLOWSTONE)
            .pattern("OOO")
            .pattern("GGG")
            .pattern("OOO")
            .unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.CHAIN)
            .define('I', Items.IRON_INGOT)
            .define('N', Items.IRON_NUGGET)
            .pattern("N")
            .pattern("I")
            .pattern("N")
            .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.TINTED_GLASS, 2)
            .define('G', Blocks.GLASS)
            .define('S', Items.AMETHYST_SHARD)
            .pattern(" S ")
            .pattern("SGS")
            .pattern(" S ")
            .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
            .save(exporter);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.AMETHYST_BLOCK, Items.AMETHYST_SHARD);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.RECOVERY_COMPASS)
            .define('C', Items.COMPASS)
            .define('S', Items.ECHO_SHARD)
            .pattern("SSS")
            .pattern("SCS")
            .pattern("SSS")
            .unlockedBy("has_echo_shard", has(Items.ECHO_SHARD))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Items.CALIBRATED_SCULK_SENSOR)
            .define('#', Items.AMETHYST_SHARD)
            .define('X', Items.SCULK_SENSOR)
            .pattern(" # ")
            .pattern("#X#")
            .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
            .save(exporter);
        threeByThreePacker(exporter, RecipeCategory.MISC, Items.MUSIC_DISC_5, Items.DISC_FRAGMENT_5);
        SpecialRecipeBuilder.special(ArmorDyeRecipe::new).save(exporter, "armor_dye");
        SpecialRecipeBuilder.special(BannerDuplicateRecipe::new).save(exporter, "banner_duplicate");
        SpecialRecipeBuilder.special(BookCloningRecipe::new).save(exporter, "book_cloning");
        SpecialRecipeBuilder.special(FireworkRocketRecipe::new).save(exporter, "firework_rocket");
        SpecialRecipeBuilder.special(FireworkStarRecipe::new).save(exporter, "firework_star");
        SpecialRecipeBuilder.special(FireworkStarFadeRecipe::new).save(exporter, "firework_star_fade");
        SpecialRecipeBuilder.special(MapCloningRecipe::new).save(exporter, "map_cloning");
        SpecialRecipeBuilder.special(MapExtendingRecipe::new).save(exporter, "map_extending");
        SpecialRecipeBuilder.special(RepairItemRecipe::new).save(exporter, "repair_item");
        SpecialRecipeBuilder.special(ShieldDecorationRecipe::new).save(exporter, "shield_decoration");
        SpecialRecipeBuilder.special(ShulkerBoxColoring::new).save(exporter, "shulker_box_coloring");
        SpecialRecipeBuilder.special(TippedArrowRecipe::new).save(exporter, "tipped_arrow");
        SpecialRecipeBuilder.special(SuspiciousStewRecipe::new).save(exporter, "suspicious_stew");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.35F, 200)
            .unlockedBy("has_potato", has(Items.POTATO))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CLAY_BALL), RecipeCategory.MISC, Items.BRICK, 0.3F, 200)
            .unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemTags.LOGS_THAT_BURN), RecipeCategory.MISC, Items.CHARCOAL, 0.15F, 200)
            .unlockedBy("has_log", has(ItemTags.LOGS_THAT_BURN))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CHORUS_FRUIT), RecipeCategory.MISC, Items.POPPED_CHORUS_FRUIT, 0.1F, 200)
            .unlockedBy("has_chorus_fruit", has(Items.CHORUS_FRUIT))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.35F, 200)
            .unlockedBy("has_beef", has(Items.BEEF))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.35F, 200)
            .unlockedBy("has_chicken", has(Items.CHICKEN))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.35F, 200)
            .unlockedBy("has_cod", has(Items.COD))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.KELP), RecipeCategory.FOOD, Items.DRIED_KELP, 0.1F, 200)
            .unlockedBy("has_kelp", has(Blocks.KELP))
            .save(exporter, getSmeltingRecipeName(Items.DRIED_KELP));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.35F, 200)
            .unlockedBy("has_salmon", has(Items.SALMON))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.35F, 200)
            .unlockedBy("has_mutton", has(Items.MUTTON))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.35F, 200)
            .unlockedBy("has_porkchop", has(Items.PORKCHOP))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.35F, 200)
            .unlockedBy("has_rabbit", has(Items.RABBIT))
            .save(exporter);
        oreSmelting(exporter, COAL_SMELTABLES, RecipeCategory.MISC, Items.COAL, 0.1F, 200, "coal");
        oreSmelting(exporter, IRON_SMELTABLES, RecipeCategory.MISC, Items.IRON_INGOT, 0.7F, 200, "iron_ingot");
        oreSmelting(exporter, COPPER_SMELTABLES, RecipeCategory.MISC, Items.COPPER_INGOT, 0.7F, 200, "copper_ingot");
        oreSmelting(exporter, GOLD_SMELTABLES, RecipeCategory.MISC, Items.GOLD_INGOT, 1.0F, 200, "gold_ingot");
        oreSmelting(exporter, DIAMOND_SMELTABLES, RecipeCategory.MISC, Items.DIAMOND, 1.0F, 200, "diamond");
        oreSmelting(exporter, LAPIS_SMELTABLES, RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.2F, 200, "lapis_lazuli");
        oreSmelting(exporter, REDSTONE_SMELTABLES, RecipeCategory.REDSTONE, Items.REDSTONE, 0.7F, 200, "redstone");
        oreSmelting(exporter, EMERALD_SMELTABLES, RecipeCategory.MISC, Items.EMERALD, 1.0F, 200, "emerald");
        nineBlockStorageRecipes(exporter, RecipeCategory.MISC, Items.RAW_IRON, RecipeCategory.BUILDING_BLOCKS, Items.RAW_IRON_BLOCK);
        nineBlockStorageRecipes(exporter, RecipeCategory.MISC, Items.RAW_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.RAW_COPPER_BLOCK);
        nineBlockStorageRecipes(exporter, RecipeCategory.MISC, Items.RAW_GOLD, RecipeCategory.BUILDING_BLOCKS, Items.RAW_GOLD_BLOCK);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemTags.SMELTS_TO_GLASS), RecipeCategory.BUILDING_BLOCKS, Blocks.GLASS.asItem(), 0.1F, 200)
            .unlockedBy("has_smelts_to_glass", has(ItemTags.SMELTS_TO_GLASS))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.SEA_PICKLE), RecipeCategory.MISC, Items.LIME_DYE, 0.1F, 200)
            .unlockedBy("has_sea_pickle", has(Blocks.SEA_PICKLE))
            .save(exporter, getSmeltingRecipeName(Items.LIME_DYE));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CACTUS.asItem()), RecipeCategory.MISC, Items.GREEN_DYE, 1.0F, 200)
            .unlockedBy("has_cactus", has(Blocks.CACTUS))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(
                    Items.GOLDEN_PICKAXE,
                    Items.GOLDEN_SHOVEL,
                    Items.GOLDEN_AXE,
                    Items.GOLDEN_HOE,
                    Items.GOLDEN_SWORD,
                    Items.GOLDEN_HELMET,
                    Items.GOLDEN_CHESTPLATE,
                    Items.GOLDEN_LEGGINGS,
                    Items.GOLDEN_BOOTS,
                    Items.GOLDEN_HORSE_ARMOR
                ),
                RecipeCategory.MISC,
                Items.GOLD_NUGGET,
                0.1F,
                200
            )
            .unlockedBy("has_golden_pickaxe", has(Items.GOLDEN_PICKAXE))
            .unlockedBy("has_golden_shovel", has(Items.GOLDEN_SHOVEL))
            .unlockedBy("has_golden_axe", has(Items.GOLDEN_AXE))
            .unlockedBy("has_golden_hoe", has(Items.GOLDEN_HOE))
            .unlockedBy("has_golden_sword", has(Items.GOLDEN_SWORD))
            .unlockedBy("has_golden_helmet", has(Items.GOLDEN_HELMET))
            .unlockedBy("has_golden_chestplate", has(Items.GOLDEN_CHESTPLATE))
            .unlockedBy("has_golden_leggings", has(Items.GOLDEN_LEGGINGS))
            .unlockedBy("has_golden_boots", has(Items.GOLDEN_BOOTS))
            .unlockedBy("has_golden_horse_armor", has(Items.GOLDEN_HORSE_ARMOR))
            .save(exporter, getSmeltingRecipeName(Items.GOLD_NUGGET));
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(
                    Items.IRON_PICKAXE,
                    Items.IRON_SHOVEL,
                    Items.IRON_AXE,
                    Items.IRON_HOE,
                    Items.IRON_SWORD,
                    Items.IRON_HELMET,
                    Items.IRON_CHESTPLATE,
                    Items.IRON_LEGGINGS,
                    Items.IRON_BOOTS,
                    Items.IRON_HORSE_ARMOR,
                    Items.CHAINMAIL_HELMET,
                    Items.CHAINMAIL_CHESTPLATE,
                    Items.CHAINMAIL_LEGGINGS,
                    Items.CHAINMAIL_BOOTS
                ),
                RecipeCategory.MISC,
                Items.IRON_NUGGET,
                0.1F,
                200
            )
            .unlockedBy("has_iron_pickaxe", has(Items.IRON_PICKAXE))
            .unlockedBy("has_iron_shovel", has(Items.IRON_SHOVEL))
            .unlockedBy("has_iron_axe", has(Items.IRON_AXE))
            .unlockedBy("has_iron_hoe", has(Items.IRON_HOE))
            .unlockedBy("has_iron_sword", has(Items.IRON_SWORD))
            .unlockedBy("has_iron_helmet", has(Items.IRON_HELMET))
            .unlockedBy("has_iron_chestplate", has(Items.IRON_CHESTPLATE))
            .unlockedBy("has_iron_leggings", has(Items.IRON_LEGGINGS))
            .unlockedBy("has_iron_boots", has(Items.IRON_BOOTS))
            .unlockedBy("has_iron_horse_armor", has(Items.IRON_HORSE_ARMOR))
            .unlockedBy("has_chainmail_helmet", has(Items.CHAINMAIL_HELMET))
            .unlockedBy("has_chainmail_chestplate", has(Items.CHAINMAIL_CHESTPLATE))
            .unlockedBy("has_chainmail_leggings", has(Items.CHAINMAIL_LEGGINGS))
            .unlockedBy("has_chainmail_boots", has(Items.CHAINMAIL_BOOTS))
            .save(exporter, getSmeltingRecipeName(Items.IRON_NUGGET));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CLAY), RecipeCategory.BUILDING_BLOCKS, Blocks.TERRACOTTA.asItem(), 0.35F, 200)
            .unlockedBy("has_clay_block", has(Blocks.CLAY))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHERRACK), RecipeCategory.MISC, Items.NETHER_BRICK, 0.1F, 200)
            .unlockedBy("has_netherrack", has(Blocks.NETHERRACK))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHER_QUARTZ_ORE), RecipeCategory.MISC, Items.QUARTZ, 0.2F, 200)
            .unlockedBy("has_nether_quartz_ore", has(Blocks.NETHER_QUARTZ_ORE))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.WET_SPONGE), RecipeCategory.BUILDING_BLOCKS, Blocks.SPONGE.asItem(), 0.15F, 200)
            .unlockedBy("has_wet_sponge", has(Blocks.WET_SPONGE))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.COBBLESTONE), RecipeCategory.BUILDING_BLOCKS, Blocks.STONE.asItem(), 0.1F, 200)
            .unlockedBy("has_cobblestone", has(Blocks.COBBLESTONE))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.STONE), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_STONE.asItem(), 0.1F, 200)
            .unlockedBy("has_stone", has(Blocks.STONE))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.SANDSTONE), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_SANDSTONE.asItem(), 0.1F, 200)
            .unlockedBy("has_sandstone", has(Blocks.SANDSTONE))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.RED_SANDSTONE), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1F, 200
            )
            .unlockedBy("has_red_sandstone", has(Blocks.RED_SANDSTONE))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.QUARTZ_BLOCK), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_QUARTZ.asItem(), 0.1F, 200)
            .unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1F, 200)
            .unlockedBy("has_stone_bricks", has(Blocks.STONE_BRICKS))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.BLACK_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_black_terracotta", has(Blocks.BLACK_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.BLUE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_blue_terracotta", has(Blocks.BLUE_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.BROWN_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_brown_terracotta", has(Blocks.BROWN_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.CYAN_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_cyan_terracotta", has(Blocks.CYAN_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.GRAY_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_gray_terracotta", has(Blocks.GRAY_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.GREEN_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_green_terracotta", has(Blocks.GREEN_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.LIGHT_BLUE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_light_blue_terracotta", has(Blocks.LIGHT_BLUE_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.LIGHT_GRAY_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_light_gray_terracotta", has(Blocks.LIGHT_GRAY_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.LIME_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_lime_terracotta", has(Blocks.LIME_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.MAGENTA_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_magenta_terracotta", has(Blocks.MAGENTA_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.ORANGE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_orange_terracotta", has(Blocks.ORANGE_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.PINK_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_pink_terracotta", has(Blocks.PINK_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.PURPLE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_purple_terracotta", has(Blocks.PURPLE_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.RED_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
            .unlockedBy("has_red_terracotta", has(Blocks.RED_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.WHITE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_white_terracotta", has(Blocks.WHITE_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Blocks.YELLOW_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
            )
            .unlockedBy("has_yellow_terracotta", has(Blocks.YELLOW_TERRACOTTA))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.ANCIENT_DEBRIS), RecipeCategory.MISC, Items.NETHERITE_SCRAP, 2.0F, 200)
            .unlockedBy("has_ancient_debris", has(Blocks.ANCIENT_DEBRIS))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BASALT), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_BASALT, 0.1F, 200)
            .unlockedBy("has_basalt", has(Blocks.BASALT))
            .save(exporter);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.COBBLED_DEEPSLATE), RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE, 0.1F, 200)
            .unlockedBy("has_cobbled_deepslate", has(Blocks.COBBLED_DEEPSLATE))
            .save(exporter);
        oreBlasting(exporter, COAL_SMELTABLES, RecipeCategory.MISC, Items.COAL, 0.1F, 100, "coal");
        oreBlasting(exporter, IRON_SMELTABLES, RecipeCategory.MISC, Items.IRON_INGOT, 0.7F, 100, "iron_ingot");
        oreBlasting(exporter, COPPER_SMELTABLES, RecipeCategory.MISC, Items.COPPER_INGOT, 0.7F, 100, "copper_ingot");
        oreBlasting(exporter, GOLD_SMELTABLES, RecipeCategory.MISC, Items.GOLD_INGOT, 1.0F, 100, "gold_ingot");
        oreBlasting(exporter, DIAMOND_SMELTABLES, RecipeCategory.MISC, Items.DIAMOND, 1.0F, 100, "diamond");
        oreBlasting(exporter, LAPIS_SMELTABLES, RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.2F, 100, "lapis_lazuli");
        oreBlasting(exporter, REDSTONE_SMELTABLES, RecipeCategory.REDSTONE, Items.REDSTONE, 0.7F, 100, "redstone");
        oreBlasting(exporter, EMERALD_SMELTABLES, RecipeCategory.MISC, Items.EMERALD, 1.0F, 100, "emerald");
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.NETHER_QUARTZ_ORE), RecipeCategory.MISC, Items.QUARTZ, 0.2F, 100)
            .unlockedBy("has_nether_quartz_ore", has(Blocks.NETHER_QUARTZ_ORE))
            .save(exporter, getBlastingRecipeName(Items.QUARTZ));
        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(
                    Items.GOLDEN_PICKAXE,
                    Items.GOLDEN_SHOVEL,
                    Items.GOLDEN_AXE,
                    Items.GOLDEN_HOE,
                    Items.GOLDEN_SWORD,
                    Items.GOLDEN_HELMET,
                    Items.GOLDEN_CHESTPLATE,
                    Items.GOLDEN_LEGGINGS,
                    Items.GOLDEN_BOOTS,
                    Items.GOLDEN_HORSE_ARMOR
                ),
                RecipeCategory.MISC,
                Items.GOLD_NUGGET,
                0.1F,
                100
            )
            .unlockedBy("has_golden_pickaxe", has(Items.GOLDEN_PICKAXE))
            .unlockedBy("has_golden_shovel", has(Items.GOLDEN_SHOVEL))
            .unlockedBy("has_golden_axe", has(Items.GOLDEN_AXE))
            .unlockedBy("has_golden_hoe", has(Items.GOLDEN_HOE))
            .unlockedBy("has_golden_sword", has(Items.GOLDEN_SWORD))
            .unlockedBy("has_golden_helmet", has(Items.GOLDEN_HELMET))
            .unlockedBy("has_golden_chestplate", has(Items.GOLDEN_CHESTPLATE))
            .unlockedBy("has_golden_leggings", has(Items.GOLDEN_LEGGINGS))
            .unlockedBy("has_golden_boots", has(Items.GOLDEN_BOOTS))
            .unlockedBy("has_golden_horse_armor", has(Items.GOLDEN_HORSE_ARMOR))
            .save(exporter, getBlastingRecipeName(Items.GOLD_NUGGET));
        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(
                    Items.IRON_PICKAXE,
                    Items.IRON_SHOVEL,
                    Items.IRON_AXE,
                    Items.IRON_HOE,
                    Items.IRON_SWORD,
                    Items.IRON_HELMET,
                    Items.IRON_CHESTPLATE,
                    Items.IRON_LEGGINGS,
                    Items.IRON_BOOTS,
                    Items.IRON_HORSE_ARMOR,
                    Items.CHAINMAIL_HELMET,
                    Items.CHAINMAIL_CHESTPLATE,
                    Items.CHAINMAIL_LEGGINGS,
                    Items.CHAINMAIL_BOOTS
                ),
                RecipeCategory.MISC,
                Items.IRON_NUGGET,
                0.1F,
                100
            )
            .unlockedBy("has_iron_pickaxe", has(Items.IRON_PICKAXE))
            .unlockedBy("has_iron_shovel", has(Items.IRON_SHOVEL))
            .unlockedBy("has_iron_axe", has(Items.IRON_AXE))
            .unlockedBy("has_iron_hoe", has(Items.IRON_HOE))
            .unlockedBy("has_iron_sword", has(Items.IRON_SWORD))
            .unlockedBy("has_iron_helmet", has(Items.IRON_HELMET))
            .unlockedBy("has_iron_chestplate", has(Items.IRON_CHESTPLATE))
            .unlockedBy("has_iron_leggings", has(Items.IRON_LEGGINGS))
            .unlockedBy("has_iron_boots", has(Items.IRON_BOOTS))
            .unlockedBy("has_iron_horse_armor", has(Items.IRON_HORSE_ARMOR))
            .unlockedBy("has_chainmail_helmet", has(Items.CHAINMAIL_HELMET))
            .unlockedBy("has_chainmail_chestplate", has(Items.CHAINMAIL_CHESTPLATE))
            .unlockedBy("has_chainmail_leggings", has(Items.CHAINMAIL_LEGGINGS))
            .unlockedBy("has_chainmail_boots", has(Items.CHAINMAIL_BOOTS))
            .save(exporter, getBlastingRecipeName(Items.IRON_NUGGET));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.ANCIENT_DEBRIS), RecipeCategory.MISC, Items.NETHERITE_SCRAP, 2.0F, 100)
            .unlockedBy("has_ancient_debris", has(Blocks.ANCIENT_DEBRIS))
            .save(exporter, getBlastingRecipeName(Items.NETHERITE_SCRAP));
        cookRecipes(exporter, "smoking", RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, 100);
        cookRecipes(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, CampfireCookingRecipe::new, 600);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_SLAB, Blocks.STONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_STAIRS, Blocks.STONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICKS, Blocks.STONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_SLAB, Blocks.STONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_STONE_BRICKS)
            .unlockedBy("has_stone", has(Blocks.STONE))
            .save(exporter, "chiseled_stone_bricks_stone_from_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), RecipeCategory.DECORATIONS, Blocks.STONE_BRICK_WALL)
            .unlockedBy("has_stone", has(Blocks.STONE))
            .save(exporter, "stone_brick_walls_from_stone_stonecutting");
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_SLAB, 2)
            .unlockedBy("has_quartz_block", has(Blocks.QUARTZ_BLOCK))
            .save(exporter, "quartz_slab_from_stonecutting");
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_STAIRS, Blocks.QUARTZ_BLOCK);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_BLOCK);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_BRICKS, Blocks.QUARTZ_BLOCK);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICKS, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICKS);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), RecipeCategory.DECORATIONS, Blocks.STONE_BRICK_WALL)
            .unlockedBy("has_stone_bricks", has(Blocks.STONE_BRICKS))
            .save(exporter, "stone_brick_wall_from_stone_bricks_stonecutting");
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_STONE_BRICKS, Blocks.STONE_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BRICK_SLAB, Blocks.BRICKS, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BRICK_STAIRS, Blocks.BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.BRICK_WALL, Blocks.BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MUD_BRICK_SLAB, Blocks.MUD_BRICKS, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MUD_BRICK_STAIRS, Blocks.MUD_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.MUD_BRICK_WALL, Blocks.MUD_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICKS, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_NETHER_BRICKS, Blocks.NETHER_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICKS, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.RED_NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_SLAB, Blocks.PURPUR_BLOCK, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_STAIRS, Blocks.PURPUR_BLOCK);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_PILLAR, Blocks.PURPUR_BLOCK);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.PRISMARINE_WALL, Blocks.PRISMARINE);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_BRICK_SLAB, 2)
            .unlockedBy("has_prismarine_brick", has(Blocks.PRISMARINE_BRICKS))
            .save(exporter, "prismarine_brick_slab_from_prismarine_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_BRICK_STAIRS)
            .unlockedBy("has_prismarine_brick", has(Blocks.PRISMARINE_BRICKS))
            .save(exporter, "prismarine_brick_stairs_from_prismarine_stonecutting");
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.ANDESITE_SLAB, Blocks.ANDESITE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.ANDESITE_WALL, Blocks.ANDESITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE, Blocks.ANDESITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_SLAB, Blocks.ANDESITE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.ANDESITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BASALT, Blocks.BASALT);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.GRANITE_SLAB, Blocks.GRANITE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.GRANITE_STAIRS, Blocks.GRANITE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.GRANITE_WALL, Blocks.GRANITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE, Blocks.GRANITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_SLAB, Blocks.GRANITE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_STAIRS, Blocks.GRANITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_STAIRS, Blocks.POLISHED_GRANITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DIORITE_SLAB, Blocks.DIORITE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DIORITE_STAIRS, Blocks.DIORITE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.DIORITE_WALL, Blocks.DIORITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE, Blocks.DIORITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_SLAB, Blocks.DIORITE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_STAIRS, Blocks.DIORITE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_STAIRS, Blocks.POLISHED_DIORITE);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICK_SLAB, 2)
            .unlockedBy("has_mossy_stone_bricks", has(Blocks.MOSSY_STONE_BRICKS))
            .save(exporter, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICK_STAIRS)
            .unlockedBy("has_mossy_stone_bricks", has(Blocks.MOSSY_STONE_BRICKS))
            .save(exporter, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), RecipeCategory.DECORATIONS, Blocks.MOSSY_STONE_BRICK_WALL)
            .unlockedBy("has_mossy_stone_bricks", has(Blocks.MOSSY_STONE_BRICKS))
            .save(exporter, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_SLAB, 2)
            .unlockedBy("has_end_stone_brick", has(Blocks.END_STONE_BRICKS))
            .save(exporter, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_STAIRS)
            .unlockedBy("has_end_stone_brick", has(Blocks.END_STONE_BRICKS))
            .save(exporter, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), RecipeCategory.DECORATIONS, Blocks.END_STONE_BRICK_WALL)
            .unlockedBy("has_end_stone_brick", has(Blocks.END_STONE_BRICKS))
            .save(exporter, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICKS, Blocks.END_STONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.END_STONE_BRICK_WALL, Blocks.END_STONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.BLACKSTONE_WALL, Blocks.BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.BLACKSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICKS, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER, Blocks.COPPER_BLOCK, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_STAIRS, Blocks.COPPER_BLOCK, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_SLAB, Blocks.COPPER_BLOCK, 8);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_COPPER, 8);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_COPPER, 8);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_COPPER, 8);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER, Blocks.WAXED_COPPER_BLOCK, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_COPPER_BLOCK, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_COPPER_BLOCK, 8);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_COPPER, 8);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_COPPER, 8);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_COPPER, 4);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_COPPER, 8);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICKS, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_BRICK_WALL, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.COBBLED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.POLISHED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.POLISHED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICKS, Blocks.POLISHED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.POLISHED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_BRICK_WALL, Blocks.POLISHED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.POLISHED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.POLISHED_DEEPSLATE);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_BRICK_WALL, Blocks.DEEPSLATE_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_BRICKS);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILES, 2);
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES);
        stonecutterResultFromBase(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_TILES);
        smithingTrims().forEach(template -> trimSmithing(exporter, template.template(), template.id()));
        netheriteSmithing(exporter, Items.DIAMOND_CHESTPLATE, RecipeCategory.COMBAT, Items.NETHERITE_CHESTPLATE);
        netheriteSmithing(exporter, Items.DIAMOND_LEGGINGS, RecipeCategory.COMBAT, Items.NETHERITE_LEGGINGS);
        netheriteSmithing(exporter, Items.DIAMOND_HELMET, RecipeCategory.COMBAT, Items.NETHERITE_HELMET);
        netheriteSmithing(exporter, Items.DIAMOND_BOOTS, RecipeCategory.COMBAT, Items.NETHERITE_BOOTS);
        netheriteSmithing(exporter, Items.DIAMOND_SWORD, RecipeCategory.COMBAT, Items.NETHERITE_SWORD);
        netheriteSmithing(exporter, Items.DIAMOND_AXE, RecipeCategory.TOOLS, Items.NETHERITE_AXE);
        netheriteSmithing(exporter, Items.DIAMOND_PICKAXE, RecipeCategory.TOOLS, Items.NETHERITE_PICKAXE);
        netheriteSmithing(exporter, Items.DIAMOND_HOE, RecipeCategory.TOOLS, Items.NETHERITE_HOE);
        netheriteSmithing(exporter, Items.DIAMOND_SHOVEL, RecipeCategory.TOOLS, Items.NETHERITE_SHOVEL);
        copySmithingTemplate(exporter, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Items.NETHERRACK);
        copySmithingTemplate(exporter, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        copySmithingTemplate(exporter, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SANDSTONE);
        copySmithingTemplate(exporter, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        copySmithingTemplate(exporter, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.MOSSY_COBBLESTONE);
        copySmithingTemplate(exporter, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
        copySmithingTemplate(exporter, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.END_STONE);
        copySmithingTemplate(exporter, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        copySmithingTemplate(exporter, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PRISMARINE);
        copySmithingTemplate(exporter, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BLACKSTONE);
        copySmithingTemplate(exporter, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.NETHERRACK);
        copySmithingTemplate(exporter, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PURPUR_BLOCK);
        copySmithingTemplate(exporter, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
        copySmithingTemplate(exporter, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
        copySmithingTemplate(exporter, Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
        copySmithingTemplate(exporter, Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
        copySmithingTemplate(exporter, Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
        threeByThreePacker(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Items.BAMBOO);
        planksFromLogs(exporter, Blocks.BAMBOO_PLANKS, ItemTags.BAMBOO_BLOCKS, 2);
        mosaicBuilder(exporter, RecipeCategory.DECORATIONS, Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_SLAB);
        woodenBoat(exporter, Items.BAMBOO_RAFT, Blocks.BAMBOO_PLANKS);
        chestBoat(exporter, Items.BAMBOO_CHEST_RAFT, Items.BAMBOO_RAFT);
        hangingSign(exporter, Items.OAK_HANGING_SIGN, Blocks.STRIPPED_OAK_LOG);
        hangingSign(exporter, Items.SPRUCE_HANGING_SIGN, Blocks.STRIPPED_SPRUCE_LOG);
        hangingSign(exporter, Items.BIRCH_HANGING_SIGN, Blocks.STRIPPED_BIRCH_LOG);
        hangingSign(exporter, Items.JUNGLE_HANGING_SIGN, Blocks.STRIPPED_JUNGLE_LOG);
        hangingSign(exporter, Items.ACACIA_HANGING_SIGN, Blocks.STRIPPED_ACACIA_LOG);
        hangingSign(exporter, Items.CHERRY_HANGING_SIGN, Blocks.STRIPPED_CHERRY_LOG);
        hangingSign(exporter, Items.DARK_OAK_HANGING_SIGN, Blocks.STRIPPED_DARK_OAK_LOG);
        hangingSign(exporter, Items.MANGROVE_HANGING_SIGN, Blocks.STRIPPED_MANGROVE_LOG);
        hangingSign(exporter, Items.BAMBOO_HANGING_SIGN, Items.STRIPPED_BAMBOO_BLOCK);
        hangingSign(exporter, Items.CRIMSON_HANGING_SIGN, Blocks.STRIPPED_CRIMSON_STEM);
        hangingSign(exporter, Items.WARPED_HANGING_SIGN, Blocks.STRIPPED_WARPED_STEM);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_BOOKSHELF)
            .define('#', ItemTags.PLANKS)
            .define('X', ItemTags.WOODEN_SLABS)
            .pattern("###")
            .pattern("XXX")
            .pattern("###")
            .unlockedBy("has_book", has(Items.BOOK))
            .save(exporter);
        oneToOneConversionRecipe(exporter, Items.ORANGE_DYE, Blocks.TORCHFLOWER, "orange_dye");
        oneToOneConversionRecipe(exporter, Items.CYAN_DYE, Blocks.PITCHER_PLANT, "cyan_dye", 2);
        planksFromLog(exporter, Blocks.CHERRY_PLANKS, ItemTags.CHERRY_LOGS, 4);
        woodFromLogs(exporter, Blocks.CHERRY_WOOD, Blocks.CHERRY_LOG);
        woodFromLogs(exporter, Blocks.STRIPPED_CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG);
        woodenBoat(exporter, Items.CHERRY_BOAT, Blocks.CHERRY_PLANKS);
        chestBoat(exporter, Items.CHERRY_CHEST_BOAT, Items.CHERRY_BOAT);
        oneToOneConversionRecipe(exporter, Items.PINK_DYE, Items.PINK_PETALS, "pink_dye", 1);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.BRUSH)
            .define('X', Items.FEATHER)
            .define('#', Items.COPPER_INGOT)
            .define('I', Items.STICK)
            .pattern("X")
            .pattern("#")
            .pattern("I")
            .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
            .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.DECORATED_POT)
            .define('#', Items.BRICK)
            .pattern(" # ")
            .pattern("# #")
            .pattern(" # ")
            .unlockedBy("has_brick", has(ItemTags.DECORATED_POT_INGREDIENTS))
            .save(exporter, "decorated_pot_simple");
        SpecialRecipeBuilder.special(DecoratedPotRecipe::new).save(exporter, "decorated_pot");
    }

    public static Stream<VanillaRecipeProvider.TrimTemplate> smithingTrims() {
        return Stream.of(
                Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE
            )
            .map(template -> new VanillaRecipeProvider.TrimTemplate(template, new ResourceLocation(getItemName(template) + "_smithing_trim")));
    }

    public static record TrimTemplate(Item template, ResourceLocation id) {
    }
}
