package net.minecraft.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

public class RegistryCodecs {
    private static <T> MapCodec<RegistryCodecs.RegistryEntry<T>> withNameAndId(ResourceKey<? extends Registry<T>> registryRef, MapCodec<T> elementCodec) {
        return RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                        ResourceKey.codec(registryRef).fieldOf("name").forGetter(RegistryCodecs.RegistryEntry::key),
                        Codec.INT.fieldOf("id").forGetter(RegistryCodecs.RegistryEntry::id),
                        elementCodec.forGetter(RegistryCodecs.RegistryEntry::value)
                    )
                    .apply(instance, RegistryCodecs.RegistryEntry::new)
        );
    }

    public static <T> Codec<Registry<T>> networkCodec(ResourceKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> elementCodec) {
        return withNameAndId(registryRef, elementCodec.fieldOf("element")).codec().listOf().xmap(entries -> {
            MappedRegistry<T> mappedRegistry = new MappedRegistry<>(registryRef, lifecycle);

            for (RegistryCodecs.RegistryEntry<T> registryEntry : entries) {
                mappedRegistry.registerMapping(registryEntry.id(), registryEntry.key(), registryEntry.value(), lifecycle);
            }

            return mappedRegistry;
        }, registry -> {
            Builder<RegistryCodecs.RegistryEntry<T>> builder = ImmutableList.builder();

            for (T object : registry) {
                builder.add(new RegistryCodecs.RegistryEntry<>(registry.getResourceKey(object).get(), registry.getId(object), object));
            }

            return builder.build();
        });
    }

    public static <E> Codec<Registry<E>> fullCodec(ResourceKey<? extends Registry<E>> registryRef, Lifecycle lifecycle, Codec<E> elementCodec) {
        Codec<Map<ResourceKey<E>, E>> codec = Codec.unboundedMap(ResourceKey.codec(registryRef), elementCodec);
        return codec.xmap(entries -> {
            WritableRegistry<E> writableRegistry = new MappedRegistry<>(registryRef, lifecycle);
            entries.forEach((key, value) -> writableRegistry.register((ResourceKey<E>)key, (E)value, lifecycle));
            return writableRegistry.freeze();
        }, registry -> ImmutableMap.copyOf(registry.entrySet()));
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
        return homogeneousList(registryRef, elementCodec, false);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean alwaysSerializeAsList) {
        return HolderSetCodec.create(registryRef, RegistryFileCodec.create(registryRef, elementCodec), alwaysSerializeAsList);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> registryRef) {
        return homogeneousList(registryRef, false);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> registryRef, boolean alwaysSerializeAsList) {
        return HolderSetCodec.create(registryRef, RegistryFixedCodec.create(registryRef), alwaysSerializeAsList);
    }

    static record RegistryEntry<T>(ResourceKey<T> key, int id, T value) {
    }
}
