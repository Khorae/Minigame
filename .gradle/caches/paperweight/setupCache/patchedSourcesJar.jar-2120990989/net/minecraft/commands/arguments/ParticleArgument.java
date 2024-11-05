package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ParticleArgument implements ArgumentType<ParticleOptions> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType(
        id -> Component.translatableEscape("particle.notFound", id)
    );
    private final HolderLookup<ParticleType<?>> particles;

    public ParticleArgument(CommandBuildContext registryAccess) {
        this.particles = registryAccess.holderLookup(Registries.PARTICLE_TYPE);
    }

    public static ParticleArgument particle(CommandBuildContext registryAccess) {
        return new ParticleArgument(registryAccess);
    }

    public static ParticleOptions getParticle(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, ParticleOptions.class);
    }

    public ParticleOptions parse(StringReader stringReader) throws CommandSyntaxException {
        return readParticle(stringReader, this.particles);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static ParticleOptions readParticle(StringReader reader, HolderLookup<ParticleType<?>> registryWrapper) throws CommandSyntaxException {
        ParticleType<?> particleType = readParticleType(reader, registryWrapper);
        return readParticle(reader, (ParticleType<ParticleOptions>)particleType);
    }

    private static ParticleType<?> readParticleType(StringReader reader, HolderLookup<ParticleType<?>> registryWrapper) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocation.read(reader);
        ResourceKey<ParticleType<?>> resourceKey = ResourceKey.create(Registries.PARTICLE_TYPE, resourceLocation);
        return registryWrapper.get(resourceKey).orElseThrow(() -> ERROR_UNKNOWN_PARTICLE.create(resourceLocation)).value();
    }

    private static <T extends ParticleOptions> T readParticle(StringReader reader, ParticleType<T> type) throws CommandSyntaxException {
        return type.getDeserializer().fromCommand(type, reader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource(this.particles.listElementIds().map(ResourceKey::location), suggestionsBuilder);
    }
}
