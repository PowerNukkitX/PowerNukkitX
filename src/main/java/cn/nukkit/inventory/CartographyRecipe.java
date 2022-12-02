package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.inventory.recipe.ItemDescriptor;
import cn.nukkit.item.Item;

import java.util.Collection;
import java.util.List;

@PowerNukkitOnly
public class CartographyRecipe extends ShapelessRecipe {

    public CartographyRecipe(Item result, Collection<Item> ingredients) {
        super(result, ingredients);
    }

    public CartographyRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
        super(recipeId, priority, result, ingredients);
    }

    public CartographyRecipe(String recipeId, int priority, Item result, List<ItemDescriptor> ingredients) {
        super(recipeId, priority, result, ingredients);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerCartographyRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.CARTOGRAPHY;
    }
}
