package cn.nukkit.inventory;

import cn.nukkit.item.Item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class FurnaceRecipe extends SmeltingRecipe {


    public FurnaceRecipe(Item result, Item ingredient) {
        super(result, ingredient);
    }

    public FurnaceRecipe(Item result, Item ingredient, String craftingTag) {
        super(result, ingredient, craftingTag);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerFurnaceRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return this.ingredient.hasMeta() ? RecipeType.FURNACE_DATA : RecipeType.FURNACE;
    }
}
