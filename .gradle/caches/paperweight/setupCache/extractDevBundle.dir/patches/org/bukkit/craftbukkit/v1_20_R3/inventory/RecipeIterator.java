package org.bukkit.craftbukkit.v1_20_R3.inventory;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.bukkit.inventory.Recipe;

public class RecipeIterator implements Iterator<Recipe> {
    private final Iterator<Map.Entry<RecipeType<?>, Object2ObjectLinkedOpenHashMap<ResourceLocation, RecipeHolder<?>>>> recipes;
    private Iterator<RecipeHolder<?>> current;
    private Recipe currentRecipe; // Paper - fix removing recipes from RecipeIterator

    public RecipeIterator() {
        this.recipes = MinecraftServer.getServer().getRecipeManager().recipes.entrySet().iterator();
    }

    @Override
    public boolean hasNext() {
        if (this.current != null && this.current.hasNext()) {
            return true;
        }

        if (this.recipes.hasNext()) {
            this.current = this.recipes.next().getValue().values().iterator();
            return this.hasNext();
        }

        return false;
    }

    @Override
    public Recipe next() {
        if (this.current == null || !this.current.hasNext()) {
            this.current = this.recipes.next().getValue().values().iterator();
            // Paper start - fix removing recipes from RecipeIterator
            this.currentRecipe = this.next();
            return this.currentRecipe;
            // Paper end - fix removing recipes from RecipeIterator
        }

        // Paper start - fix removing recipes from RecipeIterator
        this.currentRecipe = this.current.next().toBukkitRecipe();
        return this.currentRecipe;
        // Paper end - fix removing recipes from RecipeIterator
    }

    @Override
    public void remove() {
        Preconditions.checkState(this.current != null, "next() not yet called");

        // Paper start - fix removing recipes from RecipeIterator
        if (this.currentRecipe instanceof org.bukkit.Keyed keyed) {
            MinecraftServer.getServer().getRecipeManager().byName.remove(org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey.toMinecraft(keyed.getKey()));
        }
        // Paper end - fix removing recipes from RecipeIterator
        this.current.remove();
    }
}
