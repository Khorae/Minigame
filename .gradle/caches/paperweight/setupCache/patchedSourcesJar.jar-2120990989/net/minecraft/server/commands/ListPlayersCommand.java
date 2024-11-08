package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;

public class ListPlayersCommand {

    public ListPlayersCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.Commands.literal("list").executes((commandcontext) -> {
            return ListPlayersCommand.listPlayers((CommandSourceStack) commandcontext.getSource());
        })).then(net.minecraft.commands.Commands.literal("uuids").executes((commandcontext) -> {
            return ListPlayersCommand.listPlayersWithUuids((CommandSourceStack) commandcontext.getSource());
        })));
    }

    private static int listPlayers(CommandSourceStack source) {
        return ListPlayersCommand.format(source, Player::getDisplayName);
    }

    private static int listPlayersWithUuids(CommandSourceStack source) {
        return ListPlayersCommand.format(source, (entityplayer) -> {
            return Component.translatable("commands.list.nameAndId", entityplayer.getName(), Component.translationArg(entityplayer.getGameProfile().getId()));
        });
    }

    private static int format(CommandSourceStack source, Function<ServerPlayer, Component> nameProvider) {
        PlayerList playerlist = source.getServer().getPlayerList();
        // CraftBukkit start
        List<ServerPlayer> players = playerlist.getPlayers();
        if (source.getBukkitSender() instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player sender = (org.bukkit.entity.Player) source.getBukkitSender();
            players = players.stream().filter((ep) -> sender.canSee(ep.getBukkitEntity())).collect(java.util.stream.Collectors.toList());
        }
        List<ServerPlayer> list = players;
        // CraftBukkit end
        Component ichatbasecomponent = ComponentUtils.formatList(list, nameProvider);

        source.sendSuccess(() -> {
            return Component.translatable("commands.list.players", list.size(), playerlist.getMaxPlayers(), ichatbasecomponent);
        }, false);
        return list.size();
    }
}
