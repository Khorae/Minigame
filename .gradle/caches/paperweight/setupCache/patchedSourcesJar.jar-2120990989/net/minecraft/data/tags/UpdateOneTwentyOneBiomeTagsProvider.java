package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

public class UpdateOneTwentyOneBiomeTagsProvider extends TagsProvider<Biome> {
    public UpdateOneTwentyOneBiomeTagsProvider(
        PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture, CompletableFuture<TagsProvider.TagLookup<Biome>> biomeTagLookupFuture
    ) {
        super(output, Registries.BIOME, registryLookupFuture, biomeTagLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.tag(BiomeTags.HAS_TRIAL_CHAMBERS).addTag(BiomeTags.IS_OVERWORLD);
    }
}
