package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;

public class WeatherCommand {
    private static final int DEFAULT_TIME = -1;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("weather")
                .requires(source -> source.hasPermission(2))
                .then(
                    Commands.literal("clear")
                        .executes(context -> setClear(context.getSource(), -1))
                        .then(
                            Commands.argument("duration", TimeArgument.time(1))
                                .executes(context -> setClear(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))
                        )
                )
                .then(
                    Commands.literal("rain")
                        .executes(context -> setRain(context.getSource(), -1))
                        .then(
                            Commands.argument("duration", TimeArgument.time(1))
                                .executes(context -> setRain(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))
                        )
                )
                .then(
                    Commands.literal("thunder")
                        .executes(context -> setThunder(context.getSource(), -1))
                        .then(
                            Commands.argument("duration", TimeArgument.time(1))
                                .executes(context -> setThunder(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))
                        )
                )
        );
    }

    private static int getDuration(CommandSourceStack source, int duration, IntProvider provider) {
        return duration == -1 ? provider.sample(source.getLevel().getRandom()) : duration;
    }

    private static int setClear(CommandSourceStack source, int duration) {
        source.getLevel().setWeatherParameters(getDuration(source, duration, ServerLevel.RAIN_DELAY), 0, false, false);
        source.sendSuccess(() -> Component.translatable("commands.weather.set.clear"), true);
        return duration;
    }

    private static int setRain(CommandSourceStack source, int duration) {
        source.getLevel().setWeatherParameters(0, getDuration(source, duration, ServerLevel.RAIN_DURATION), true, false);
        source.sendSuccess(() -> Component.translatable("commands.weather.set.rain"), true);
        return duration;
    }

    private static int setThunder(CommandSourceStack source, int duration) {
        source.getLevel().setWeatherParameters(0, getDuration(source, duration, ServerLevel.THUNDER_DURATION), true, true);
        source.sendSuccess(() -> Component.translatable("commands.weather.set.thunder"), true);
        return duration;
    }
}
