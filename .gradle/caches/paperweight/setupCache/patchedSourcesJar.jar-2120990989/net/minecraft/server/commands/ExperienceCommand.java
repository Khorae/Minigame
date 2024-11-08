package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ExperienceCommand {
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(
        Component.translatable("commands.experience.set.points.invalid")
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> literalCommandNode = dispatcher.register(
            Commands.literal("experience")
                .requires(source -> source.hasPermission(2))
                .then(
                    Commands.literal("add")
                        .then(
                            Commands.argument("targets", EntityArgument.players())
                                .then(
                                    Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(
                                            context -> addExperience(
                                                    context.getSource(),
                                                    EntityArgument.getPlayers(context, "targets"),
                                                    IntegerArgumentType.getInteger(context, "amount"),
                                                    ExperienceCommand.Type.POINTS
                                                )
                                        )
                                        .then(
                                            Commands.literal("points")
                                                .executes(
                                                    context -> addExperience(
                                                            context.getSource(),
                                                            EntityArgument.getPlayers(context, "targets"),
                                                            IntegerArgumentType.getInteger(context, "amount"),
                                                            ExperienceCommand.Type.POINTS
                                                        )
                                                )
                                        )
                                        .then(
                                            Commands.literal("levels")
                                                .executes(
                                                    context -> addExperience(
                                                            context.getSource(),
                                                            EntityArgument.getPlayers(context, "targets"),
                                                            IntegerArgumentType.getInteger(context, "amount"),
                                                            ExperienceCommand.Type.LEVELS
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("set")
                        .then(
                            Commands.argument("targets", EntityArgument.players())
                                .then(
                                    Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(
                                            context -> setExperience(
                                                    context.getSource(),
                                                    EntityArgument.getPlayers(context, "targets"),
                                                    IntegerArgumentType.getInteger(context, "amount"),
                                                    ExperienceCommand.Type.POINTS
                                                )
                                        )
                                        .then(
                                            Commands.literal("points")
                                                .executes(
                                                    context -> setExperience(
                                                            context.getSource(),
                                                            EntityArgument.getPlayers(context, "targets"),
                                                            IntegerArgumentType.getInteger(context, "amount"),
                                                            ExperienceCommand.Type.POINTS
                                                        )
                                                )
                                        )
                                        .then(
                                            Commands.literal("levels")
                                                .executes(
                                                    context -> setExperience(
                                                            context.getSource(),
                                                            EntityArgument.getPlayers(context, "targets"),
                                                            IntegerArgumentType.getInteger(context, "amount"),
                                                            ExperienceCommand.Type.LEVELS
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("query")
                        .then(
                            Commands.argument("targets", EntityArgument.player())
                                .then(
                                    Commands.literal("points")
                                        .executes(
                                            context -> queryExperience(
                                                    context.getSource(), EntityArgument.getPlayer(context, "targets"), ExperienceCommand.Type.POINTS
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("levels")
                                        .executes(
                                            context -> queryExperience(
                                                    context.getSource(), EntityArgument.getPlayer(context, "targets"), ExperienceCommand.Type.LEVELS
                                                )
                                        )
                                )
                        )
                )
        );
        dispatcher.register(Commands.literal("xp").requires(source -> source.hasPermission(2)).redirect(literalCommandNode));
    }

    private static int queryExperience(CommandSourceStack source, ServerPlayer player, ExperienceCommand.Type component) {
        int i = component.query.applyAsInt(player);
        source.sendSuccess(() -> Component.translatable("commands.experience.query." + component.name, player.getDisplayName(), i), false);
        return i;
    }

    private static int addExperience(CommandSourceStack source, Collection<? extends ServerPlayer> targets, int amount, ExperienceCommand.Type component) {
        for (ServerPlayer serverPlayer : targets) {
            component.add.accept(serverPlayer, amount);
        }

        if (targets.size() == 1) {
            source.sendSuccess(
                () -> Component.translatable(
                        "commands.experience.add." + component.name + ".success.single", amount, targets.iterator().next().getDisplayName()
                    ),
                true
            );
        } else {
            source.sendSuccess(() -> Component.translatable("commands.experience.add." + component.name + ".success.multiple", amount, targets.size()), true);
        }

        return targets.size();
    }

    private static int setExperience(CommandSourceStack source, Collection<? extends ServerPlayer> targets, int amount, ExperienceCommand.Type component) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer serverPlayer : targets) {
            if (component.set.test(serverPlayer, amount)) {
                i++;
            }
        }

        if (i == 0) {
            throw ERROR_SET_POINTS_INVALID.create();
        } else {
            if (targets.size() == 1) {
                source.sendSuccess(
                    () -> Component.translatable(
                            "commands.experience.set." + component.name + ".success.single", amount, targets.iterator().next().getDisplayName()
                        ),
                    true
                );
            } else {
                source.sendSuccess(
                    () -> Component.translatable("commands.experience.set." + component.name + ".success.multiple", amount, targets.size()), true
                );
            }

            return targets.size();
        }
    }

    static enum Type {
        POINTS("points", Player::giveExperiencePoints, (player, xp) -> {
            if (xp >= player.getXpNeededForNextLevel()) {
                return false;
            } else {
                player.setExperiencePoints(xp);
                return true;
            }
        }, player -> Mth.floor(player.experienceProgress * (float)player.getXpNeededForNextLevel())),
        LEVELS("levels", ServerPlayer::giveExperienceLevels, (player, level) -> {
            player.setExperienceLevels(level);
            return true;
        }, player -> player.experienceLevel);

        public final BiConsumer<ServerPlayer, Integer> add;
        public final BiPredicate<ServerPlayer, Integer> set;
        public final String name;
        final ToIntFunction<ServerPlayer> query;

        private Type(String name, BiConsumer<ServerPlayer, Integer> adder, BiPredicate<ServerPlayer, Integer> setter, ToIntFunction<ServerPlayer> getter) {
            this.add = adder;
            this.name = name;
            this.set = setter;
            this.query = getter;
        }
    }
}
