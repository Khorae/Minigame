package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ServerboundRecipeBookSeenRecipePacket implements Packet<ServerGamePacketListener> {
    private final ResourceLocation recipe;

    public ServerboundRecipeBookSeenRecipePacket(RecipeHolder<?> recipe) {
        this.recipe = recipe.id();
    }

    public ServerboundRecipeBookSeenRecipePacket(FriendlyByteBuf buf) {
        this.recipe = buf.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.recipe);
    }

    @Override
    public void handle(ServerGamePacketListener listener) {
        listener.handleRecipeBookSeenRecipePacket(this);
    }

    public ResourceLocation getRecipe() {
        return this.recipe;
    }
}
