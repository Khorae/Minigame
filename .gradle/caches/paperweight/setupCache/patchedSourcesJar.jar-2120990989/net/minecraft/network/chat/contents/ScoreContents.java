package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;

public class ScoreContents implements ComponentContents {
    public static final MapCodec<ScoreContents> INNER_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ScoreContents::getName), Codec.STRING.fieldOf("objective").forGetter(ScoreContents::getObjective)
                )
                .apply(instance, ScoreContents::new)
    );
    public static final MapCodec<ScoreContents> CODEC = INNER_CODEC.fieldOf("score");
    public static final ComponentContents.Type<ScoreContents> TYPE = new ComponentContents.Type<>(CODEC, "score");
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;

    @Nullable
    private static EntitySelector parseSelector(String name) {
        try {
            return new EntitySelectorParser(new StringReader(name)).parse();
        } catch (CommandSyntaxException var2) {
            return null;
        }
    }

    public ScoreContents(String name, String objective) {
        this.name = name;
        this.selector = parseSelector(name);
        this.objective = objective;
    }

    @Override
    public ComponentContents.Type<?> type() {
        return TYPE;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
    }

    public String getObjective() {
        return this.objective;
    }

    private ScoreHolder findTargetName(CommandSourceStack source) throws CommandSyntaxException {
        if (this.selector != null) {
            List<? extends Entity> list = this.selector.findEntities(source);
            if (!list.isEmpty()) {
                if (list.size() != 1) {
                    throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
                }

                return list.get(0);
            }
        }

        return ScoreHolder.forNameOnly(this.name);
    }

    private MutableComponent getScore(ScoreHolder scoreHolder, CommandSourceStack source) {
        MinecraftServer minecraftServer = source.getServer();
        if (minecraftServer != null) {
            Scoreboard scoreboard = minecraftServer.getScoreboard();
            Objective objective = scoreboard.getObjective(this.objective);
            if (objective != null) {
                ReadOnlyScoreInfo readOnlyScoreInfo = scoreboard.getPlayerScoreInfo(scoreHolder, objective);
                if (readOnlyScoreInfo != null) {
                    return readOnlyScoreInfo.formatValue(objective.numberFormatOrDefault(StyledFormat.NO_STYLE));
                }
            }
        }

        return Component.empty();
    }

    @Override
    public MutableComponent resolve(@Nullable CommandSourceStack source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source == null) {
            return Component.empty();
        } else {
            ScoreHolder scoreHolder = this.findTargetName(source);
            ScoreHolder scoreHolder2 = (ScoreHolder)(sender != null && scoreHolder.equals(ScoreHolder.WILDCARD) ? sender : scoreHolder);
            return this.getScore(scoreHolder2, source);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            if (object instanceof ScoreContents scoreContents && this.name.equals(scoreContents.name) && this.objective.equals(scoreContents.objective)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        int i = this.name.hashCode();
        return 31 * i + this.objective.hashCode();
    }

    @Override
    public String toString() {
        return "score{name='" + this.name + "', objective='" + this.objective + "'}";
    }
}
