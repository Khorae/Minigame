package net.minecraft.advancements.critereon;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public record EntityTypePredicate(HolderSet<EntityType<?>> types) {
    public static final Codec<EntityTypePredicate> CODEC = Codec.either(
            TagKey.hashedCodec(Registries.ENTITY_TYPE), BuiltInRegistries.ENTITY_TYPE.holderByNameCodec()
        )
        .flatComapMap(
            either -> either.map(
                    tag -> new EntityTypePredicate(BuiltInRegistries.ENTITY_TYPE.getOrCreateTag((TagKey<EntityType<?>>)tag)),
                    type -> new EntityTypePredicate(HolderSet.direct(type))
                ),
            predicate -> {
                HolderSet<EntityType<?>> holderSet = predicate.types();
                Optional<TagKey<EntityType<?>>> optional = holderSet.unwrapKey();
                if (optional.isPresent()) {
                    return DataResult.success(Either.left(optional.get()));
                } else {
                    return holderSet.size() == 1
                        ? DataResult.success(Either.right(holderSet.get(0)))
                        : DataResult.error(() -> "Entity type set must have a single element, but got " + holderSet.size());
                }
            }
        );

    public static EntityTypePredicate of(EntityType<?> type) {
        return new EntityTypePredicate(HolderSet.direct(type.builtInRegistryHolder()));
    }

    public static EntityTypePredicate of(TagKey<EntityType<?>> tag) {
        return new EntityTypePredicate(BuiltInRegistries.ENTITY_TYPE.getOrCreateTag(tag));
    }

    public boolean matches(EntityType<?> type) {
        return type.is(this.types);
    }
}
