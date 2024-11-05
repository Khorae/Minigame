package net.minecraft.commands;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext {
    <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> registryRef);

    static CommandBuildContext simple(HolderLookup.Provider wrapperLookup, FeatureFlagSet enabledFeatures) {
        return new CommandBuildContext() {
            @Override
            public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> registryRef) {
                return wrapperLookup.lookupOrThrow(registryRef).filterFeatures(enabledFeatures);
            }
        };
    }

    static CommandBuildContext.Configurable configurable(RegistryAccess registryManager, FeatureFlagSet enabledFeatures) {
        return new CommandBuildContext.Configurable() {
            CommandBuildContext.MissingTagAccessPolicy missingTagAccessPolicy = CommandBuildContext.MissingTagAccessPolicy.FAIL;

            @Override
            public void missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy entryListCreationPolicy) {
                this.missingTagAccessPolicy = entryListCreationPolicy;
            }

            @Override
            public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> registryRef) {
                Registry<T> registry = registryManager.registryOrThrow(registryRef);
                final HolderLookup.RegistryLookup<T> registryLookup = registry.asLookup();
                final HolderLookup.RegistryLookup<T> registryLookup2 = registry.asTagAddingLookup();
                HolderLookup.RegistryLookup<T> registryLookup3 = new HolderLookup.RegistryLookup.Delegate<T>() {
                    @Override
                    protected HolderLookup.RegistryLookup<T> parent() {
                        return switch (missingTagAccessPolicy) {
                            case FAIL -> registryLookup;
                            case CREATE_NEW -> registryLookup2;
                        };
                    }
                };
                return registryLookup3.filterFeatures(enabledFeatures);
            }
        };
    }

    public interface Configurable extends CommandBuildContext {
        void missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy entryListCreationPolicy);
    }

    public static enum MissingTagAccessPolicy {
        CREATE_NEW,
        FAIL;
    }
}
