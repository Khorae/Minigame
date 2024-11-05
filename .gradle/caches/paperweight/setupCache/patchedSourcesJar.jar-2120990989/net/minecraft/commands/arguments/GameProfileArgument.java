package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class GameProfileArgument implements ArgumentType<GameProfileArgument.Result> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
    public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(Component.translatable("argument.player.unknown"));

    public static Collection<GameProfile> getGameProfiles(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, GameProfileArgument.Result.class).getNames(context.getSource());
    }

    public static GameProfileArgument gameProfile() {
        return new GameProfileArgument();
    }

    public GameProfileArgument.Result parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '@') {
            EntitySelectorParser entitySelectorParser = new EntitySelectorParser(stringReader);
            EntitySelector entitySelector = entitySelectorParser.parse();
            if (entitySelector.includesEntities()) {
                throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.create();
            } else {
                return new GameProfileArgument.SelectorResult(entitySelector);
            }
        } else {
            int i = stringReader.getCursor();

            while (stringReader.canRead() && stringReader.peek() != ' ') {
                stringReader.skip();
            }

            String string = stringReader.getString().substring(i, stringReader.getCursor());
            return source -> {
                Optional<GameProfile> optional = source.getServer().getProfileCache().get(string);
                return Collections.singleton(optional.orElseThrow(ERROR_UNKNOWN_PLAYER::create));
            };
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (commandContext.getSource() instanceof SharedSuggestionProvider) {
            StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
            stringReader.setCursor(suggestionsBuilder.getStart());
            EntitySelectorParser entitySelectorParser = new EntitySelectorParser(stringReader);

            try {
                entitySelectorParser.parse();
            } catch (CommandSyntaxException var6) {
            }

            return entitySelectorParser.fillSuggestions(
                suggestionsBuilder,
                builder -> SharedSuggestionProvider.suggest(((SharedSuggestionProvider)commandContext.getSource()).getOnlinePlayerNames(), builder)
            );
        } else {
            return Suggestions.empty();
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @FunctionalInterface
    public interface Result {
        Collection<GameProfile> getNames(CommandSourceStack source) throws CommandSyntaxException;
    }

    public static class SelectorResult implements GameProfileArgument.Result {
        private final EntitySelector selector;

        public SelectorResult(EntitySelector selector) {
            this.selector = selector;
        }

        @Override
        public Collection<GameProfile> getNames(CommandSourceStack source) throws CommandSyntaxException {
            List<ServerPlayer> list = this.selector.findPlayers(source);
            if (list.isEmpty()) {
                throw EntityArgument.NO_PLAYERS_FOUND.create();
            } else {
                List<GameProfile> list2 = Lists.newArrayList();

                for (ServerPlayer serverPlayer : list) {
                    list2.add(serverPlayer.getGameProfile());
                }

                return list2;
            }
        }
    }
}