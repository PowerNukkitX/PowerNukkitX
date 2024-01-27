package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecipe implements Recipe {
    protected final String id;
    protected final List<Item> results = new ArrayList<>();
    protected final List<ItemDescriptor> ingredients = new ArrayList<>();

    protected BaseRecipe(String id) {
        this.id = id;
    }

    @Override
    public @NotNull String getRecipeId() {
        return id;
    }

    @Override
    public List<Item> getResults() {
        return results;
    }

    @Override
    public List<ItemDescriptor> getIngredients() {
        return ingredients;
    }
}
