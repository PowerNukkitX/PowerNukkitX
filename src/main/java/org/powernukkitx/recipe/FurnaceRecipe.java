package org.powernukkitx.recipe;

import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class FurnaceRecipe extends SmeltingRecipe {
    public FurnaceRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }

    public FurnaceRecipe(@Nullable String recipeId, Item result, Item ingredient) {
        super(recipeId == null ?
                RecipeRegistry.computeRecipeId(List.of(result), List.of(new DefaultDescriptor(ingredient)), RecipeType.FURNACE) :
                recipeId
        );
        this.ingredients.add(new DefaultDescriptor(ingredient.clone()));
        this.results.add(result.clone());
    }

    public FurnaceRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, ItemDescriptor ingredient) {
        super(recipeId, uuid, netId, priority);
        this.ingredients.add(ingredient);
        this.results.add(result.clone());
    }

    @Override
    public boolean match(Input input) {
        return false;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.FURNACE;
    }

    @Override
    public String getRecipeIdTag() {
        return "furnace";
    }
}
