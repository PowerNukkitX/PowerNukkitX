package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ShapelessRecipe extends CraftingRecipe {
    public ShapelessRecipe(Item result, Collection<Item> ingredients) {
        this(null, 10, result, ingredients);
    }

    public ShapelessRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
        this(recipeId, priority, result, ingredients.stream().map(item -> (ItemDescriptor) new DefaultDescriptor(item)).toList());
    }

    public ShapelessRecipe(String recipeId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, null, priority, result, ingredients);
    }

    public ShapelessRecipe(String recipeId, UUID uuid, int priority, Item result, List<ItemDescriptor> ingredients) {
        super(recipeId, priority);
        this.uuid = uuid;
        this.results.add(result.clone());
        if (ingredients.size() > 9) {
            throw new IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients");
        }
        this.ingredients.addAll(ingredients);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS;
    }

    @Override
    public boolean match(Input input) {
        return false;
    }
}
