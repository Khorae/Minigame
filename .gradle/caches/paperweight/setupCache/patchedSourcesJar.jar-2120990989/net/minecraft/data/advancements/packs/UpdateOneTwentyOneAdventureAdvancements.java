package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.world.entity.EntityType;

public class UpdateOneTwentyOneAdventureAdvancements implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider lookup, Consumer<AdvancementHolder> exporter) {
        AdvancementHolder advancementHolder = AdvancementSubProvider.createPlaceholder("adventure/root");
        VanillaAdventureAdvancements.createMonsterHunterAdvancement(
            advancementHolder,
            exporter,
            Stream.concat(VanillaAdventureAdvancements.MOBS_TO_KILL.stream(), Stream.of(EntityType.BREEZE)).collect(Collectors.toList())
        );
    }
}