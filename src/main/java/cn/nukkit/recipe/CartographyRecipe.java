package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.RecipeRegistry;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static cn.nukkit.recipe.RecipeType.CARTOGRAPHY;


public class CartographyRecipe extends ShapelessRecipe {
    /**
     * @deprecated 
     */
    

    public CartographyRecipe(Item result, Collection<Item> ingredients) {
        this(null, 10, result, ingredients);
    }
    /**
     * @deprecated 
     */
    

    public CartographyRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
        this(recipeId, priority, result, ingredients.stream().map(item -> (ItemDescriptor) new DefaultDescriptor(item)).toList());
    }
    /**
     * @deprecated 
     */
    

    public CartographyRecipe(String recipeId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, null, priority, result, ingredients);
    }
    /**
     * @deprecated 
     */
    

    public CartographyRecipe(String recipeId, UUID uuid, int priority, Item result, List<ItemDescriptor> ingredients) {
        super(recipeId == null ? RecipeRegistry.computeRecipeId(List.of(result), ingredients, CARTOGRAPHY) : recipeId, uuid, priority, result, ingredients);
    }

    @Override
    public RecipeType getType() {
        return CARTOGRAPHY;
    }
}
