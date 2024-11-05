package net.minecraft.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public interface Registry<T> extends Keyable, IdMap<T> {
    ResourceKey<? extends Registry<T>> key();

    default Codec<T> byNameCodec() {
        Codec<T> codec = ResourceLocation.CODEC
            .flatXmap(
                id -> Optional.ofNullable(this.get(id))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + this.key() + ": " + id)),
                value -> this.getResourceKey((T)value)
                        .map(ResourceKey::location)
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown registry element in " + this.key() + ":" + value))
            );
        Codec<T> codec2 = ExtraCodecs.idResolverCodec(value -> this.getResourceKey(value).isPresent() ? this.getId(value) : -1, this::byId, -1);
        return ExtraCodecs.overrideLifecycle(ExtraCodecs.orCompressed(codec, codec2), this::lifecycle, this::lifecycle);
    }

    default Codec<Holder<T>> holderByNameCodec() {
        Codec<Holder<T>> codec = ResourceLocation.CODEC
            .flatXmap(
                id -> this.getHolder(ResourceKey.create(this.key(), id))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + this.key() + ": " + id)),
                entry -> entry.unwrapKey()
                        .map(ResourceKey::location)
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown registry element in " + this.key() + ":" + entry))
            );
        return ExtraCodecs.overrideLifecycle(codec, entry -> this.lifecycle(entry.value()), entry -> this.lifecycle(entry.value()));
    }

    default <U> Stream<U> keys(DynamicOps<U> dynamicOps) {
        return this.keySet().stream().map(id -> dynamicOps.createString(id.toString()));
    }

    @Nullable
    ResourceLocation getKey(T value);

    Optional<ResourceKey<T>> getResourceKey(T entry);

    @Override
    int getId(@Nullable T value);

    @Nullable
    T get(@Nullable ResourceKey<T> key);

    @Nullable
    T get(@Nullable ResourceLocation id);

    Lifecycle lifecycle(T entry);

    Lifecycle registryLifecycle();

    default Optional<T> getOptional(@Nullable ResourceLocation id) {
        return Optional.ofNullable(this.get(id));
    }

    default Optional<T> getOptional(@Nullable ResourceKey<T> key) {
        return Optional.ofNullable(this.get(key));
    }

    default T getOrThrow(ResourceKey<T> key) {
        T object = this.get(key);
        if (object == null) {
            throw new IllegalStateException("Missing key in " + this.key() + ": " + key);
        } else {
            return object;
        }
    }

    Set<ResourceLocation> keySet();

    Set<Entry<ResourceKey<T>, T>> entrySet();

    Set<ResourceKey<T>> registryKeySet();

    Optional<Holder.Reference<T>> getRandom(RandomSource random);

    default Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    boolean containsKey(ResourceLocation id);

    boolean containsKey(ResourceKey<T> key);

    static <T> T register(Registry<? super T> registry, String id, T entry) {
        return register(registry, new ResourceLocation(id), entry);
    }

    static <V, T extends V> T register(Registry<V> registry, ResourceLocation id, T entry) {
        return register(registry, ResourceKey.create(registry.key(), id), entry);
    }

    static <V, T extends V> T register(Registry<V> registry, ResourceKey<V> key, T entry) {
        ((WritableRegistry)registry).register(key, (V)entry, Lifecycle.stable());
        return entry;
    }

    static <T> Holder.Reference<T> registerForHolder(Registry<T> registry, ResourceKey<T> key, T entry) {
        return ((WritableRegistry)registry).register(key, entry, Lifecycle.stable());
    }

    static <T> Holder.Reference<T> registerForHolder(Registry<T> registry, ResourceLocation id, T entry) {
        return registerForHolder(registry, ResourceKey.create(registry.key(), id), entry);
    }

    Registry<T> freeze();

    Holder.Reference<T> createIntrusiveHolder(T value);

    Optional<Holder.Reference<T>> getHolder(int rawId);

    Optional<Holder.Reference<T>> getHolder(ResourceKey<T> key);

    Holder<T> wrapAsHolder(T value);

    default Holder.Reference<T> getHolderOrThrow(ResourceKey<T> key) {
        return this.getHolder(key).orElseThrow(() -> new IllegalStateException("Missing key in " + this.key() + ": " + key));
    }

    Stream<Holder.Reference<T>> holders();

    Optional<HolderSet.Named<T>> getTag(TagKey<T> tag);

    default Iterable<Holder<T>> getTagOrEmpty(TagKey<T> tag) {
        return DataFixUtils.orElse(this.getTag(tag), List.<T>of());
    }

    HolderSet.Named<T> getOrCreateTag(TagKey<T> tag);

    Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags();

    Stream<TagKey<T>> getTagNames();

    void resetTags();

    void bindTags(Map<TagKey<T>, List<Holder<T>>> tagEntries);

    default IdMap<Holder<T>> asHolderIdMap() {
        return new IdMap<Holder<T>>() {
            @Override
            public int getId(Holder<T> value) {
                return Registry.this.getId(value.value());
            }

            @Nullable
            @Override
            public Holder<T> byId(int i) {
                return (Holder<T>)Registry.this.getHolder(i).orElse(null);
            }

            @Override
            public int size() {
                return Registry.this.size();
            }

            @Override
            public Iterator<Holder<T>> iterator() {
                return Registry.this.holders().map(entry -> (Holder<T>)entry).iterator();
            }
        };
    }

    HolderOwner<T> holderOwner();

    HolderLookup.RegistryLookup<T> asLookup();

    default HolderLookup.RegistryLookup<T> asTagAddingLookup() {
        return new HolderLookup.RegistryLookup.Delegate<T>() {
            @Override
            protected HolderLookup.RegistryLookup<T> parent() {
                return Registry.this.asLookup();
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> tag) {
                return Optional.of(this.getOrThrow(tag));
            }

            @Override
            public HolderSet.Named<T> getOrThrow(TagKey<T> tag) {
                return Registry.this.getOrCreateTag(tag);
            }
        };
    }
}
