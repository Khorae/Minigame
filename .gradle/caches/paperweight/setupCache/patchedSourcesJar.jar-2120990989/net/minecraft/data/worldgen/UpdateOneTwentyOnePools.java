package net.minecraft.data.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class UpdateOneTwentyOnePools {
    public static final ResourceKey<StructureTemplatePool> EMPTY = createKey("empty");

    public static ResourceKey<StructureTemplatePool> createKey(String id) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(id));
    }

    public static void register(BootstapContext<StructureTemplatePool> structurePoolsRegisterable, String id, StructureTemplatePool pool) {
        Pools.register(structurePoolsRegisterable, id, pool);
    }

    public static void bootstrap(BootstapContext<StructureTemplatePool> structurePoolsRegisterable) {
        TrialChambersStructurePools.bootstrap(structurePoolsRegisterable);
    }
}
