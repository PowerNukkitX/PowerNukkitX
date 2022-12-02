package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;

@PowerNukkitOnly
public class BlastFurnaceRecipe extends SmeltingRecipe {


    public BlastFurnaceRecipe(Item result, Item ingredient) {
        super(result, ingredient);
    }

    public BlastFurnaceRecipe(Item result, Item ingredient, String craftingTag) {
        super(result, ingredient, craftingTag);
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
