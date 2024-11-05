package net.minecraft.data.loot;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataResolver;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackOutput.PathProvider pathProvider;
    private final Set<ResourceLocation> requiredTables;
    private final List<LootTableProvider.SubProviderEntry> subProviders;

    public LootTableProvider(PackOutput output, Set<ResourceLocation> lootTableIds, List<LootTableProvider.SubProviderEntry> lootTypeGenerators) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
        this.subProviders = lootTypeGenerators;
        this.requiredTables = lootTableIds;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        final Map<ResourceLocation, LootTable> map = Maps.newHashMap();
        Map<RandomSupport.Seed128bit, ResourceLocation> map2 = new Object2ObjectOpenHashMap<>();
        this.subProviders.forEach(generator -> generator.provider().get().generate((id, builder) -> {
                ResourceLocation resourceLocationx = map2.put(RandomSequence.seedForKey(id), id);
                if (resourceLocationx != null) {
                    Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + resourceLocationx + " and " + id);
                }

                builder.setRandomSequence(id);
                if (map.put(id, builder.setParamSet(generator.paramSet).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + id);
                }
            }));
        ProblemReporter.Collector collector = new ProblemReporter.Collector();
        ValidationContext validationContext = new ValidationContext(collector, LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
            @Nullable
            @Override
            public <T> T getElement(LootDataId<T> key) {
                return (T)(key.type() == LootDataType.TABLE ? map.get(key.location()) : null);
            }
        });

        for (ResourceLocation resourceLocation : Sets.difference(this.requiredTables, map.keySet())) {
            collector.report("Missing built-in table: " + resourceLocation);
        }

        map.forEach(
            (id, table) -> table.validate(
                    validationContext.setParams(table.getParamSet()).enterElement("{" + id + "}", new LootDataId<>(LootDataType.TABLE, id))
                )
        );
        Multimap<String, String> multimap = collector.get();
        if (!multimap.isEmpty()) {
            multimap.forEach((name, message) -> LOGGER.warn("Found validation problem in {}: {}", name, message));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        } else {
            return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
                ResourceLocation resourceLocationx = entry.getKey();
                LootTable lootTable = entry.getValue();
                Path path = this.pathProvider.json(resourceLocationx);
                return DataProvider.saveStable(writer, LootTable.CODEC, lootTable, path);
            }).toArray(CompletableFuture[]::new));
        }
    }

    @Override
    public final String getName() {
        return "Loot Tables";
    }

    public static record SubProviderEntry(Supplier<LootTableSubProvider> provider, LootContextParamSet paramSet) {
    }
}
