package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import javax.annotation.Nullable;
import java.util.List;


public class BlastFurnaceRecipe implements SmeltingRecipe {
    private final Item output;
    private Item ingredient;
    private final String recipeId;


    public BlastFurnaceRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }

    public BlastFurnaceRecipe(@Nullable String recipeId, Item result, Item ingredient) {
        this.recipeId = recipeId == null ? CraftingManager.getMultiItemHash(List.of(ingredient, result)).toString() : recipeId;
        this.output = result.clone();
        this.ingredient = ingredient.clone();
    }

    public void setInput(Item item) {
        this.ingredient = item.clone();
    }

    @Override
    public Item getInput() {
        return this.ingredient.clone();
    }

    @Override
    public String getRecipeId() {
        return null;
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerBlastFurnaceRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return this.ingredient.hasMeta() ? RecipeType.BLAST_FURNACE_DATA : RecipeType.BLAST_FURNACE;
    }
}
