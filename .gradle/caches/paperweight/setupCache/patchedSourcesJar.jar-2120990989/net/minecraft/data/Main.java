package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.WorldVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.advancements.packs.UpdateOneTwentyOneAdvancementProvider;
import net.minecraft.data.advancements.packs.VanillaAdvancementProvider;
import net.minecraft.data.info.BiomeParametersDumpReport;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.data.info.CommandsReport;
import net.minecraft.data.info.RegistryDumpReport;
import net.minecraft.data.loot.packs.TradeRebalanceLootTableProvider;
import net.minecraft.data.loot.packs.UpdateOneTwentyOneLootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.recipes.packs.BundleRecipeProvider;
import net.minecraft.data.recipes.packs.UpdateOneTwentyOneRecipeProvider;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.UpdateOneTwentyOneRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.CatVariantTagsProvider;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FlatLevelGeneratorPresetTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.GameEventTagsProvider;
import net.minecraft.data.tags.InstrumentTagsProvider;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.TradeRebalanceStructureTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyOneBiomeTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyOneBlockTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyOneDamageTypeTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyOneEntityTypeTagsProvider;
import net.minecraft.data.tags.UpdateOneTwentyOneItemTagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.data.tags.WorldPresetTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class Main {
    @DontObfuscate
    public static void main(String[] args) throws IOException {
        SharedConstants.tryDetectVersion();
        OptionParser optionParser = new OptionParser();
        OptionSpec<Void> optionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
        OptionSpec<Void> optionSpec2 = optionParser.accepts("server", "Include server generators");
        OptionSpec<Void> optionSpec3 = optionParser.accepts("client", "Include client generators");
        OptionSpec<Void> optionSpec4 = optionParser.accepts("dev", "Include development tools");
        OptionSpec<Void> optionSpec5 = optionParser.accepts("reports", "Include data reports");
        OptionSpec<Void> optionSpec6 = optionParser.accepts("validate", "Validate inputs");
        OptionSpec<Void> optionSpec7 = optionParser.accepts("all", "Include all generators");
        OptionSpec<String> optionSpec8 = optionParser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated");
        OptionSpec<String> optionSpec9 = optionParser.accepts("input", "Input folder").withRequiredArg();
        OptionSet optionSet = optionParser.parse(args);
        if (!optionSet.has(optionSpec) && optionSet.hasOptions()) {
            Path path = Paths.get(optionSpec8.value(optionSet));
            boolean bl = optionSet.has(optionSpec7);
            boolean bl2 = bl || optionSet.has(optionSpec3);
            boolean bl3 = bl || optionSet.has(optionSpec2);
            boolean bl4 = bl || optionSet.has(optionSpec4);
            boolean bl5 = bl || optionSet.has(optionSpec5);
            boolean bl6 = bl || optionSet.has(optionSpec6);
            DataGenerator dataGenerator = createStandardGenerator(
                path,
                optionSet.valuesOf(optionSpec9).stream().map(input -> Paths.get(input)).collect(Collectors.toList()),
                bl2,
                bl3,
                bl4,
                bl5,
                bl6,
                SharedConstants.getCurrentVersion(),
                true
            );
            dataGenerator.run();
        } else {
            optionParser.printHelpOn(System.out);
        }
    }

    private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(
        BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> baseFactory, CompletableFuture<HolderLookup.Provider> registryLookupFuture
    ) {
        return output -> baseFactory.apply(output, registryLookupFuture);
    }

    public static DataGenerator createStandardGenerator(
        Path output,
        Collection<Path> inputs,
        boolean includeClient,
        boolean includeServer,
        boolean includeDev,
        boolean includeReports,
        boolean validate,
        WorldVersion gameVersion,
        boolean ignoreCache
    ) {
        DataGenerator dataGenerator = new DataGenerator(output, gameVersion, ignoreCache);
        DataGenerator.PackGenerator packGenerator = dataGenerator.getVanillaPack(includeClient || includeServer);
        packGenerator.addProvider(outputx -> new SnbtToNbt(outputx, inputs).addFilter(new StructureUpdater()));
        CompletableFuture<HolderLookup.Provider> completableFuture = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        DataGenerator.PackGenerator packGenerator2 = dataGenerator.getVanillaPack(includeClient);
        packGenerator2.addProvider(ModelProvider::new);
        DataGenerator.PackGenerator packGenerator3 = dataGenerator.getVanillaPack(includeServer);
        packGenerator3.addProvider(bindRegistries(RegistriesDatapackGenerator::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(VanillaAdvancementProvider::create, completableFuture));
        packGenerator3.addProvider(VanillaLootTableProvider::create);
        packGenerator3.addProvider(VanillaRecipeProvider::new);
        TagsProvider<Block> tagsProvider = packGenerator3.addProvider(bindRegistries(VanillaBlockTagsProvider::new, completableFuture));
        TagsProvider<Item> tagsProvider2 = packGenerator3.addProvider(
            outputx -> new VanillaItemTagsProvider(outputx, completableFuture, tagsProvider.contentsGetter())
        );
        TagsProvider<Biome> tagsProvider3 = packGenerator3.addProvider(bindRegistries(BiomeTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(BannerPatternTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(CatVariantTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(DamageTypeTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(EntityTypeTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(FlatLevelGeneratorPresetTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(FluidTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(GameEventTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(InstrumentTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(PaintingVariantTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(PoiTypeTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(StructureTagsProvider::new, completableFuture));
        packGenerator3.addProvider(bindRegistries(WorldPresetTagsProvider::new, completableFuture));
        packGenerator3 = dataGenerator.getVanillaPack(includeDev);
        packGenerator3.addProvider(outputx -> new NbtToSnbt(outputx, inputs));
        packGenerator3 = dataGenerator.getVanillaPack(includeReports);
        packGenerator3.addProvider(bindRegistries(BiomeParametersDumpReport::new, completableFuture));
        packGenerator3.addProvider(BlockListReport::new);
        packGenerator3.addProvider(bindRegistries(CommandsReport::new, completableFuture));
        packGenerator3.addProvider(RegistryDumpReport::new);
        packGenerator3 = dataGenerator.getBuiltinDatapack(includeServer, "bundle");
        packGenerator3.addProvider(BundleRecipeProvider::new);
        packGenerator3.addProvider(
            outputx -> PackMetadataGenerator.forFeaturePack(
                    outputx, Component.translatable("dataPack.bundle.description"), FeatureFlagSet.of(FeatureFlags.BUNDLE)
                )
        );
        packGenerator3 = dataGenerator.getBuiltinDatapack(includeServer, "trade_rebalance");
        packGenerator3.addProvider(
            outputx -> PackMetadataGenerator.forFeaturePack(
                    outputx, Component.translatable("dataPack.trade_rebalance.description"), FeatureFlagSet.of(FeatureFlags.TRADE_REBALANCE)
                )
        );
        packGenerator3.addProvider(TradeRebalanceLootTableProvider::create);
        packGenerator3.addProvider(bindRegistries(TradeRebalanceStructureTagsProvider::new, completableFuture));
        CompletableFuture<RegistrySetBuilder.PatchedRegistries> completableFuture2 = UpdateOneTwentyOneRegistries.createLookup(completableFuture);
        CompletableFuture<HolderLookup.Provider> completableFuture3 = completableFuture2.thenApply(RegistrySetBuilder.PatchedRegistries::full);
        CompletableFuture<HolderLookup.Provider> completableFuture4 = completableFuture2.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        DataGenerator.PackGenerator packGenerator8 = dataGenerator.getBuiltinDatapack(includeServer, "update_1_21");
        packGenerator8.addProvider(UpdateOneTwentyOneRecipeProvider::new);
        TagsProvider<Block> tagsProvider4 = packGenerator8.addProvider(
            outputx -> new UpdateOneTwentyOneBlockTagsProvider(outputx, completableFuture4, tagsProvider.contentsGetter())
        );
        packGenerator8.addProvider(
            outputx -> new UpdateOneTwentyOneItemTagsProvider(outputx, completableFuture4, tagsProvider2.contentsGetter(), tagsProvider4.contentsGetter())
        );
        packGenerator8.addProvider(outputx -> new UpdateOneTwentyOneBiomeTagsProvider(outputx, completableFuture4, tagsProvider3.contentsGetter()));
        packGenerator8.addProvider(UpdateOneTwentyOneLootTableProvider::create);
        packGenerator8.addProvider(bindRegistries(RegistriesDatapackGenerator::new, completableFuture4));
        packGenerator8.addProvider(
            outputx -> PackMetadataGenerator.forFeaturePack(
                    outputx, Component.translatable("dataPack.update_1_21.description"), FeatureFlagSet.of(FeatureFlags.UPDATE_1_21)
                )
        );
        packGenerator8.addProvider(bindRegistries(UpdateOneTwentyOneEntityTypeTagsProvider::new, completableFuture3));
        packGenerator8.addProvider(bindRegistries(UpdateOneTwentyOneDamageTypeTagsProvider::new, completableFuture3));
        packGenerator8.addProvider(bindRegistries(UpdateOneTwentyOneAdvancementProvider::create, completableFuture3));
        return dataGenerator;
    }
}