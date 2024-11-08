package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public class WorldPresetTagsProvider extends TagsProvider<WorldPreset> {
    public WorldPresetTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.WORLD_PRESET, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.tag(WorldPresetTags.NORMAL)
            .add(WorldPresets.NORMAL)
            .add(WorldPresets.FLAT)
            .add(WorldPresets.LARGE_BIOMES)
            .add(WorldPresets.AMPLIFIED)
            .add(WorldPresets.SINGLE_BIOME_SURFACE);
        this.tag(WorldPresetTags.EXTENDED).addTag(WorldPresetTags.NORMAL).add(WorldPresets.DEBUG);
    }
}
