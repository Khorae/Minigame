package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ClientboundUpdateRecipesPacket implements Packet<ClientGamePacketListener> {
    private final List<RecipeHolder<?>> recipes;

    public ClientboundUpdateRecipesPacket(Collection<RecipeHolder<?>> recipes) {
        this.recipes = Lists.newArrayList(recipes);
    }

    public ClientboundUpdateRecipesPacket(FriendlyByteBuf buf) {
        this.recipes = buf.readList(ClientboundUpdateRecipesPacket::fromNetwork);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(this.recipes, ClientboundUpdateRecipesPacket::toNetwork);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleUpdateRecipes(this);
    }

    public List<RecipeHolder<?>> getRecipes() {
        return this.recipes;
    }

    private static RecipeHolder<?> fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation resourceLocation = buf.readResourceLocation();
        ResourceLocation resourceLocation2 = buf.readResourceLocation();
        Recipe<?> recipe = BuiltInRegistries.RECIPE_SERIALIZER
            .getOptional(resourceLocation)
            .orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + resourceLocation))
            .fromNetwork(buf);
        return new RecipeHolder<>(resourceLocation2, recipe);
    }

    public static <T extends Recipe<?>> void toNetwork(FriendlyByteBuf buf, RecipeHolder<?> recipe) {
        buf.writeResourceLocation(BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.value().getSerializer()));
        buf.writeResourceLocation(recipe.id());
        ((RecipeSerializer<Recipe<?>>)recipe.value().getSerializer()).toNetwork(buf, recipe.value());
    }
}
