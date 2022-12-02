package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;

@PowerNukkitOnly
public class SmokerRecipe extends SmeltingRecipe {


    public SmokerRecipe(Item result, Item ingredient) {
        super(result, ingredient);
    }

    public SmokerRecipe(Item result, Item ingredient, String craftingTag) {
        super(result, ingredient, craftingTag);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerSmokerRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return this.ingredient.hasMeta() ? RecipeType.SMOKER_DATA : RecipeType.SMOKER;
    }
}
