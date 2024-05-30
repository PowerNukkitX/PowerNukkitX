package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.registry.RecipeRegistry;

import java.util.List;

public class BlastFurnaceRecipe extends SmeltingRecipe {
    public BlastFurnaceRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }


    public BlastFurnaceRecipe(String recipeId, Item result, Item ingredient) {
        super(recipeId == null ?
                RecipeRegistry.computeRecipeId(List.of(result), List.of(new DefaultDescriptor(ingredient)),
                        ingredient.hasMeta() ? RecipeType.BLAST_FURNACE_DATA : RecipeType.BLAST_FURNACE) :
                recipeId);
        this.ingredients.add(new DefaultDescriptor(ingredient.clone()));
        this.results.add(result.clone());
    }

    @Override
    public boolean match(Input input) {
        return false;
    }

    @Override
    public RecipeType getType() {
        if (this.getInput() instanceof DefaultDescriptor des) {
            return des.getItem().hasMeta() ? RecipeType.BLAST_FURNACE_DATA : RecipeType.BLAST_FURNACE;
        } else return RecipeType.BLAST_FURNACE;
    }
}
