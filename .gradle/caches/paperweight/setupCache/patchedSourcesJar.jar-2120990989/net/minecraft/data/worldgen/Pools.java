package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class Pools {
    public static final ResourceKey<StructureTemplatePool> EMPTY = createKey("empty");

    public static ResourceKey<StructureTemplatePool> createKey(String id) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(id));
    }

    public static void register(BootstapContext<StructureTemplatePool> structurePoolsRegisterable, String id, StructureTemplatePool pool) {
        structurePoolsRegisterable.register(createKey(id), pool);
    }

    public static void bootstrap(BootstapContext<StructureTemplatePool> structurePoolsRegisterable) {
        HolderGetter<StructureTemplatePool> holderGetter = structurePoolsRegisterable.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> holder = holderGetter.getOrThrow(EMPTY);
        structurePoolsRegisterable.register(EMPTY, new StructureTemplatePool(holder, ImmutableList.of(), StructureTemplatePool.Projection.RIGID));
        BastionPieces.bootstrap(structurePoolsRegisterable);
        PillagerOutpostPools.bootstrap(structurePoolsRegisterable);
        VillagePools.bootstrap(structurePoolsRegisterable);
        AncientCityStructurePieces.bootstrap(structurePoolsRegisterable);
        TrailRuinsStructurePools.bootstrap(structurePoolsRegisterable);
    }
}
