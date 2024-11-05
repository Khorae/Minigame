package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

public class RegistrySynchronization {
    private static final Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> NETWORKABLE_REGISTRIES = Util.make(() -> {
        Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder = ImmutableMap.builder();
        put(builder, Registries.BIOME, Biome.NETWORK_CODEC);
        put(builder, Registries.CHAT_TYPE, ChatType.CODEC);
        put(builder, Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC);
        put(builder, Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC);
        put(builder, Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC);
        put(builder, Registries.DAMAGE_TYPE, DamageType.CODEC);
        return builder.build();
    });
    public static final Codec<RegistryAccess> NETWORK_CODEC = makeNetworkCodec();

    private static <E> void put(
        Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder,
        ResourceKey<? extends Registry<E>> key,
        Codec<E> networkCodec
    ) {
        builder.put(key, new RegistrySynchronization.NetworkedRegistryData<>(key, networkCodec));
    }

    private static Stream<RegistryAccess.RegistryEntry<?>> ownedNetworkableRegistries(RegistryAccess dynamicRegistryManager) {
        return dynamicRegistryManager.registries().filter(entry -> NETWORKABLE_REGISTRIES.containsKey(entry.key()));
    }

    private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends Registry<E>> registryRef) {
        return Optional.ofNullable(NETWORKABLE_REGISTRIES.get(registryRef))
            .map(info -> info.networkCodec())
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(() -> "Unknown or not serializable registry: " + registryRef));
    }

    private static <E> Codec<RegistryAccess> makeNetworkCodec() {
        Codec<ResourceKey<? extends Registry<E>>> codec = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
        Codec<Registry<E>> codec2 = codec.partialDispatch(
            "type",
            registry -> DataResult.success(registry.key()),
            registryRef -> getNetworkCodec((ResourceKey<? extends Registry<E>>)registryRef)
                    .map(codecx -> RegistryCodecs.networkCodec((ResourceKey<? extends Registry<E>>)registryRef, Lifecycle.experimental(), codecx))
        );
        UnboundedMapCodec<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> unboundedMapCodec = Codec.unboundedMap(codec, codec2);
        return captureMap(unboundedMapCodec);
    }

    private static <K extends ResourceKey<? extends Registry<?>>, V extends Registry<?>> Codec<RegistryAccess> captureMap(UnboundedMapCodec<K, V> networkCodec) {
        return networkCodec.xmap(
            RegistryAccess.ImmutableRegistryAccess::new,
            registryManager -> ownedNetworkableRegistries(registryManager).collect(ImmutableMap.toImmutableMap(entry -> entry.key(), entry -> entry.value()))
        );
    }

    public static Stream<RegistryAccess.RegistryEntry<?>> networkedRegistries(LayeredRegistryAccess<RegistryLayer> combinedRegistries) {
        return ownedNetworkableRegistries(combinedRegistries.getAccessFrom(RegistryLayer.WORLDGEN));
    }

    public static Stream<RegistryAccess.RegistryEntry<?>> networkSafeRegistries(LayeredRegistryAccess<RegistryLayer> combinedRegistries) {
        Stream<RegistryAccess.RegistryEntry<?>> stream = combinedRegistries.getLayer(RegistryLayer.STATIC).registries();
        Stream<RegistryAccess.RegistryEntry<?>> stream2 = networkedRegistries(combinedRegistries);
        return Stream.concat(stream2, stream);
    }

    static record NetworkedRegistryData<E>(ResourceKey<? extends Registry<E>> key, Codec<E> networkCodec) {
    }
}
