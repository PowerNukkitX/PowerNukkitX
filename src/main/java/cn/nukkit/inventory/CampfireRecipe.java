package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;

@PowerNukkitOnly
public class CampfireRecipe extends SmeltingRecipe {

    public CampfireRecipe(Item result, Item ingredient) {
        super(result, ingredient);
    }

    public CampfireRecipe(Item result, Item ingredient, String craftingTag) {
        super(result, ingredient, craftingTag);
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
