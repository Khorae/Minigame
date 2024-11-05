package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;

public interface HolderLookup<T> extends HolderGetter<T> {
    Stream<Holder.Reference<T>> listElements();

    default Stream<ResourceKey<T>> listElementIds() {
        return this.listElements().map(Holder.Reference::key);
    }

    Stream<HolderSet.Named<T>> listTags();

    default Stream<TagKey<T>> listTagIds() {
        return this.listTags().map(HolderSet.Named::key);
    }

    default HolderLookup<T> filterElements(Predicate<T> filter) {
        return new HolderLookup.Delegate<T>(this) {
            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> key) {
                return this.parent.get(key).filter(entry -> filter.test(entry.value()));
            }

            @Override
            public Stream<Holder.Reference<T>> listElements() {
                return this.parent.listElements().filter(entry -> filter.test(entry.value()));
            }
        };
    }

    public static class Delegate<T> implements HolderLookup<T> {
        protected final HolderLookup<T> parent;

        public Delegate(HolderLookup<T> baseWrapper) {
            this.parent = baseWrapper;
        }

        @Override
        public Optional<Holder.Reference<T>> get(ResourceKey<T> key) {
            return this.parent.get(key);
        }

        @Override
        public Stream<Holder.Reference<T>> listElements() {
            return this.parent.listElements();
        }

        @Override
        public Optional<HolderSet.Named<T>> get(TagKey<T> tag) {
            return this.parent.get(tag);
        }

        @Override
        public Stream<HolderSet.Named<T>> listTags() {
            return this.parent.listTags();
        }
    }

    public interface Provider {
        Stream<ResourceKey<? extends Registry<?>>> listRegistries();

        <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryRef);

        default <T> HolderLookup.RegistryLookup<T> lookupOrThrow(ResourceKey<? extends Registry<? extends T>> registryRef) {
            return this.lookup(registryRef).orElseThrow(() -> new IllegalStateException("Registry " + registryRef.location() + " not found"));
        }

        default HolderGetter.Provider asGetterLookup() {
            return new HolderGetter.Provider() {
                @Override
                public <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryRef) {
                    return Provider.this.lookup(registryRef).map(lookup -> (HolderGetter<T>)lookup);
                }
            };
        }

        static HolderLookup.Provider create(Stream<HolderLookup.RegistryLookup<?>> wrappers) {
            final Map<ResourceKey<? extends Registry<?>>, HolderLookup.RegistryLookup<?>> map = wrappers.collect(
                Collectors.toUnmodifiableMap(HolderLookup.RegistryLookup::key, wrapper -> wrapper)
            );
            return new HolderLookup.Provider() {
                @Override
                public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
                    return map.keySet().stream();
                }

                @Override
                public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryRef) {
                    return Optional.ofNullable((HolderLookup.RegistryLookup<T>)map.get(registryRef));
                }
            };
        }
    }

    public interface RegistryLookup<T> extends HolderLookup<T>, HolderOwner<T> {
        ResourceKey<? extends Registry<? extends T>> key();

        Lifecycle registryLifecycle();

        default HolderLookup<T> filterFeatures(FeatureFlagSet enabledFeatures) {
            return (HolderLookup<T>)(FeatureElement.FILTERED_REGISTRIES.contains(this.key())
                ? this.filterElements(feature -> ((FeatureElement)feature).isEnabled(enabledFeatures))
                : this);
        }

        public abstract static class Delegate<T> implements HolderLookup.RegistryLookup<T> {
            protected abstract HolderLookup.RegistryLookup<T> parent();

            @Override
            public ResourceKey<? extends Registry<? extends T>> key() {
                return this.parent().key();
            }

            @Override
            public Lifecycle registryLifecycle() {
                return this.parent().registryLifecycle();
            }

            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> key) {
                return this.parent().get(key);
            }

            @Override
            public Stream<Holder.Reference<T>> listElements() {
                return this.parent().listElements();
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> tag) {
                return this.parent().get(tag);
            }

            @Override
            public Stream<HolderSet.Named<T>> listTags() {
                return this.parent().listTags();
            }
        }
    }
}
