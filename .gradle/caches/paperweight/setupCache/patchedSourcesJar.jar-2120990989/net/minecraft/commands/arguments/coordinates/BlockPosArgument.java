package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class BlockPosArgument implements ArgumentType<Coordinates> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType(Component.translatable("argument.pos.unloaded"));
    public static final SimpleCommandExceptionType ERROR_OUT_OF_WORLD = new SimpleCommandExceptionType(Component.translatable("argument.pos.outofworld"));
    public static final SimpleCommandExceptionType ERROR_OUT_OF_BOUNDS = new SimpleCommandExceptionType(Component.translatable("argument.pos.outofbounds"));

    public static BlockPosArgument blockPos() {
        return new BlockPosArgument();
    }

    public static BlockPos getLoadedBlockPos(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerLevel serverLevel = context.getSource().getLevel();
        return getLoadedBlockPos(context, serverLevel, name);
    }

    public static BlockPos getLoadedBlockPos(CommandContext<CommandSourceStack> context, ServerLevel world, String name) throws CommandSyntaxException {
        BlockPos blockPos = getBlockPos(context, name);
        if (!world.hasChunkAt(blockPos)) {
            throw ERROR_NOT_LOADED.create();
        } else if (!world.isInWorldBounds(blockPos)) {
            throw ERROR_OUT_OF_WORLD.create();
        } else {
            return blockPos;
        }
    }

    public static BlockPos getBlockPos(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Coordinates.class).getBlockPos(context.getSource());
    }

    public static BlockPos getSpawnablePos(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        BlockPos blockPos = getBlockPos(context, name);
        if (!Level.isInSpawnableBounds(blockPos)) {
            throw ERROR_OUT_OF_BOUNDS.create();
        } else {
            return blockPos;
        }
    }

    public Coordinates parse(StringReader stringReader) throws CommandSyntaxException {
        return (Coordinates)(stringReader.canRead() && stringReader.peek() == '^'
            ? LocalCoordinates.parse(stringReader)
            : WorldCoordinates.parseInt(stringReader));
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (!(commandContext.getSource() instanceof SharedSuggestionProvider)) {
            return Suggestions.empty();
        } else {
            String string = suggestionsBuilder.getRemaining();
            Collection<SharedSuggestionProvider.TextCoordinates> collection;
            if (!string.isEmpty() && string.charAt(0) == '^') {
                collection = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
            } else {
                collection = ((SharedSuggestionProvider)commandContext.getSource()).getRelevantCoordinates();
            }

            return SharedSuggestionProvider.suggestCoordinates(string, collection, suggestionsBuilder, Commands.createValidator(this::parse));
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
