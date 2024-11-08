package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ResourceOrTagKeyArgument<T> implements ArgumentType<ResourceOrTagKeyArgument.Result<T>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
    final ResourceKey<? extends Registry<T>> registryKey;

    public ResourceOrTagKeyArgument(ResourceKey<? extends Registry<T>> registryRef) {
        this.registryKey = registryRef;
    }

    public static <T> ResourceOrTagKeyArgument<T> resourceOrTagKey(ResourceKey<? extends Registry<T>> registryRef) {
        return new ResourceOrTagKeyArgument<>(registryRef);
    }

    public static <T> ResourceOrTagKeyArgument.Result<T> getResourceOrTagKey(
        CommandContext<CommandSourceStack> context, String name, ResourceKey<Registry<T>> registryRef, DynamicCommandExceptionType invalidException
    ) throws CommandSyntaxException {
        ResourceOrTagKeyArgument.Result<?> result = context.getArgument(name, ResourceOrTagKeyArgument.Result.class);
        Optional<ResourceOrTagKeyArgument.Result<T>> optional = result.cast(registryRef);
        return optional.orElseThrow(() -> invalidException.create(result));
    }

    public ResourceOrTagKeyArgument.Result<T> parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '#') {
            int i = stringReader.getCursor();

            try {
                stringReader.skip();
                ResourceLocation resourceLocation = ResourceLocation.read(stringReader);
                return new ResourceOrTagKeyArgument.TagResult<>(TagKey.create(this.registryKey, resourceLocation));
            } catch (CommandSyntaxException var4) {
                stringReader.setCursor(i);
                throw var4;
            }
        } else {
            ResourceLocation resourceLocation2 = ResourceLocation.read(stringReader);
            return new ResourceOrTagKeyArgument.ResourceResult<>(ResourceKey.create(this.registryKey, resourceLocation2));
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return commandContext.getSource() instanceof SharedSuggestionProvider sharedSuggestionProvider
            ? sharedSuggestionProvider.suggestRegistryElements(
                this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ALL, suggestionsBuilder, commandContext
            )
            : suggestionsBuilder.buildFuture();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static class Info<T> implements ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ResourceOrTagKeyArgument.Info<T>.Template> {
        @Override
        public void serializeToNetwork(ResourceOrTagKeyArgument.Info<T>.Template properties, FriendlyByteBuf buf) {
            buf.writeResourceKey(properties.registryKey);
        }

        @Override
        public ResourceOrTagKeyArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
            return new ResourceOrTagKeyArgument.Info.Template(friendlyByteBuf.readRegistryKey());
        }

        @Override
        public void serializeToJson(ResourceOrTagKeyArgument.Info<T>.Template properties, JsonObject json) {
            json.addProperty("registry", properties.registryKey.location().toString());
        }

        @Override
        public ResourceOrTagKeyArgument.Info<T>.Template unpack(ResourceOrTagKeyArgument<T> argumentType) {
            return new ResourceOrTagKeyArgument.Info.Template(argumentType.registryKey);
        }

        public final class Template implements ArgumentTypeInfo.Template<ResourceOrTagKeyArgument<T>> {
            final ResourceKey<? extends Registry<T>> registryKey;

            Template(ResourceKey<? extends Registry<T>> registryRef) {
                this.registryKey = registryRef;
            }

            @Override
            public ResourceOrTagKeyArgument<T> instantiate(CommandBuildContext commandBuildContext) {
                return new ResourceOrTagKeyArgument<>(this.registryKey);
            }

            @Override
            public ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ?> type() {
                return Info.this;
            }
        }
    }

    static record ResourceResult<T>(ResourceKey<T> key) implements ResourceOrTagKeyArgument.Result<T> {
        @Override
        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.left(this.key);
        }

        @Override
        public <E> Optional<ResourceOrTagKeyArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> registryRef) {
            return this.key.cast(registryRef).map(ResourceOrTagKeyArgument.ResourceResult::new);
        }

        @Override
        public boolean test(Holder<T> holder) {
            return holder.is(this.key);
        }

        @Override
        public String asPrintable() {
            return this.key.location().toString();
        }
    }

    public interface Result<T> extends Predicate<Holder<T>> {
        Either<ResourceKey<T>, TagKey<T>> unwrap();

        <E> Optional<ResourceOrTagKeyArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> registryRef);

        String asPrintable();
    }

    static record TagResult<T>(TagKey<T> key) implements ResourceOrTagKeyArgument.Result<T> {
        @Override
        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.right(this.key);
        }

        @Override
        public <E> Optional<ResourceOrTagKeyArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> registryRef) {
            return this.key.cast(registryRef).map(ResourceOrTagKeyArgument.TagResult::new);
        }

        @Override
        public boolean test(Holder<T> holder) {
            return holder.is(this.key);
        }

        @Override
        public String asPrintable() {
            return "#" + this.key.location();
        }
    }
}
