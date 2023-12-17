package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.item.Item;

import javax.annotation.Nullable;
import java.util.List;


public class CampfireRecipe implements SmeltingRecipe {
    private final Item output;
    private Item ingredient;
    private final String recipeId;


    public CampfireRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }


    public CampfireRecipe(@Nullable String recipeId, Item result, Item ingredient) {
        this.recipeId = recipeId == null ? CraftingManager.getMultiItemHash(List.of(ingredient, result)).toString() : recipeId;
        this.output = result.clone();
        this.ingredient = ingredient.clone();
    }

    @Override
    public String getRecipeId() {
        return recipeId;
    }


    public void setInput(Item item) {
        this.ingredient = item.clone();
    }


    @Override
    public Item getInput() {
        return this.ingredient.clone();
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerCampfireRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return this.ingredient.hasMeta() ? RecipeType.CAMPFIRE_DATA : RecipeType.CAMPFIRE;
    }
}
