package net.minecraft.recipebook;

import java.util.Iterator;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

public interface PlaceRecipe<T> {
    default void placeRecipe(int gridWidth, int gridHeight, int gridOutputSlot, RecipeHolder<?> recipe, Iterator<T> inputs, int amount) {
        int i = gridWidth;
        int j = gridHeight;
        if (recipe.value() instanceof ShapedRecipe shapedRecipe) {
            i = shapedRecipe.getWidth();
            j = shapedRecipe.getHeight();
        }

        int k = 0;

        for (int l = 0; l < gridHeight; l++) {
            if (k == gridOutputSlot) {
                k++;
            }

            boolean bl = (float)j < (float)gridHeight / 2.0F;
            int m = Mth.floor((float)gridHeight / 2.0F - (float)j / 2.0F);
            if (bl && m > l) {
                k += gridWidth;
                l++;
            }

            for (int n = 0; n < gridWidth; n++) {
                if (!inputs.hasNext()) {
                    return;
                }

                bl = (float)i < (float)gridWidth / 2.0F;
                m = Mth.floor((float)gridWidth / 2.0F - (float)i / 2.0F);
                int o = i;
                boolean bl2 = n < i;
                if (bl) {
                    o = m + i;
                    bl2 = m <= n && n < m + i;
                }

                if (bl2) {
                    this.addItemToSlot(inputs, k, amount, l, n);
                } else if (o == n) {
                    k += gridWidth - n;
                    break;
                }

                k++;
            }
        }
    }

    void addItemToSlot(Iterator<T> inputs, int slot, int amount, int gridX, int gridY);
}
