package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType(
        id -> Component.translatableEscape("advancement.advancementNotFound", id)
    );
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType(
        id -> Component.translatableEscape("recipe.notFound", id)
    );
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType(
        id -> Component.translatableEscape("predicate.unknown", id)
    );
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM_MODIFIER = new DynamicCommandExceptionType(
        id -> Component.translatableEscape("item_modifier.unknown", id)
    );

    public static ResourceLocationArgument id() {
        return new ResourceLocationArgument();
    }

    public static AdvancementHolder getAdvancement(CommandContext<CommandSourceStack> context, String argumentName) throws CommandSyntaxException {
        ResourceLocation resourceLocation = getId(context, argumentName);
        AdvancementHolder advancementHolder = context.getSource().getServer().getAdvancements().get(resourceLocation);
        if (advancementHolder == null) {
            throw ERROR_UNKNOWN_ADVANCEMENT.create(resourceLocation);
        } else {
            return advancementHolder;
        }
    }

    public static RecipeHolder<?> getRecipe(CommandContext<CommandSourceStack> context, String argumentName) throws CommandSyntaxException {
        RecipeManager recipeManager = context.getSource().getServer().getRecipeManager();
        ResourceLocation resourceLocation = getId(context, argumentName);
        return recipeManager.byKey(resourceLocation).orElseThrow(() -> ERROR_UNKNOWN_RECIPE.create(resourceLocation));
    }

    public static LootItemCondition getPredicate(CommandContext<CommandSourceStack> context, String argumentName) throws CommandSyntaxException {
        ResourceLocation resourceLocation = getId(context, argumentName);
        LootDataManager lootDataManager = context.getSource().getServer().getLootData();
        LootItemCondition lootItemCondition = lootDataManager.getElement(LootDataType.PREDICATE, resourceLocation);
        if (lootItemCondition == null) {
            throw ERROR_UNKNOWN_PREDICATE.create(resourceLocation);
        } else {
            return lootItemCondition;
        }
    }

    public static LootItemFunction getItemModifier(CommandContext<CommandSourceStack> context, String argumentName) throws CommandSyntaxException {
        ResourceLocation resourceLocation = getId(context, argumentName);
        LootDataManager lootDataManager = context.getSource().getServer().getLootData();
        LootItemFunction lootItemFunction = lootDataManager.getElement(LootDataType.MODIFIER, resourceLocation);
        if (lootItemFunction == null) {
            throw ERROR_UNKNOWN_ITEM_MODIFIER.create(resourceLocation);
        } else {
            return lootItemFunction;
        }
    }

    public static ResourceLocation getId(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, ResourceLocation.class);
    }

    public ResourceLocation parse(StringReader stringReader) throws CommandSyntaxException {
        return ResourceLocation.read(stringReader);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
