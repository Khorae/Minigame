package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPredicateArgument implements ArgumentType<ItemPredicateArgument.Result> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
    private final HolderLookup<Item> items;

    public ItemPredicateArgument(CommandBuildContext commandRegistryAccess) {
        this.items = commandRegistryAccess.holderLookup(Registries.ITEM);
    }

    public static ItemPredicateArgument itemPredicate(CommandBuildContext commandRegistryAccess) {
        return new ItemPredicateArgument(commandRegistryAccess);
    }

    public ItemPredicateArgument.Result parse(StringReader stringReader) throws CommandSyntaxException {
        Either<ItemParser.ItemResult, ItemParser.TagResult> either = ItemParser.parseForTesting(this.items, stringReader);
        return either.map(item -> createResult(item2 -> item2 == item.item(), item.nbt()), tag -> createResult(tag.tag()::contains, tag.nbt()));
    }

    public static Predicate<ItemStack> getItemPredicate(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, ItemPredicateArgument.Result.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return ItemParser.fillSuggestions(this.items, suggestionsBuilder, true);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static ItemPredicateArgument.Result createResult(Predicate<Holder<Item>> predicate, @Nullable CompoundTag nbt) {
        return nbt != null ? stack -> stack.is(predicate) && NbtUtils.compareNbt(nbt, stack.getTag(), true) : stack -> stack.is(predicate);
    }

    public interface Result extends Predicate<ItemStack> {
    }
}
