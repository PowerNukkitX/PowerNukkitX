package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;

import java.util.Collection;

@PowerNukkitOnly
public class CartographyRecipe extends ShapelessRecipe {
    @PowerNukkitOnly
    public CartographyRecipe(Item result, Collection<Item> ingredients) {
        super(result, ingredients);
    }

    @PowerNukkitOnly
    public CartographyRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
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
