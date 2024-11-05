package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class SetWorldSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("setworldspawn")
                .requires(source -> source.hasPermission(2))
                .executes(context -> setSpawn(context.getSource(), BlockPos.containing(context.getSource().getPosition()), 0.0F))
                .then(
                    Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(context -> setSpawn(context.getSource(), BlockPosArgument.getSpawnablePos(context, "pos"), 0.0F))
                        .then(
                            Commands.argument("angle", AngleArgument.angle())
                                .executes(
                                    context -> setSpawn(
                                            context.getSource(), BlockPosArgument.getSpawnablePos(context, "pos"), AngleArgument.getAngle(context, "angle")
                                        )
                                )
                        )
                )
        );
    }

    private static int setSpawn(CommandSourceStack source, BlockPos pos, float angle) {
        source.getLevel().setDefaultSpawnPos(pos, angle);
        source.sendSuccess(() -> Component.translatable("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ(), angle), true);
        return 1;
    }
}
