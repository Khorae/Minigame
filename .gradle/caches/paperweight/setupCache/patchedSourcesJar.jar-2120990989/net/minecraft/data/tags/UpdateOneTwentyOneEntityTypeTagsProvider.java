package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class UpdateOneTwentyOneEntityTypeTagsProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {
    public UpdateOneTwentyOneEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.ENTITY_TYPE, registryLookupFuture, entityType -> entityType.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(EntityType.BREEZE);
        this.tag(EntityTypeTags.DEFLECTS_ARROWS).add(EntityType.BREEZE);
        this.tag(EntityTypeTags.DEFLECTS_TRIDENTS).add(EntityType.BREEZE);
        this.tag(EntityTypeTags.CAN_TURN_IN_BOATS).add(EntityType.BREEZE);
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(EntityType.WIND_CHARGE);
    }
}
