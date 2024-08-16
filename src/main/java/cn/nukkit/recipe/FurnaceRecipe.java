package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.registry.RecipeRegistry;

import javax.annotation.Nullable;
import java.util.List;

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

    @Override
    public boolean match(Input input) {
        return false;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.FURNACE;
    }
}
