package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.ItemDescriptor;

public abstract class SmeltingRecipe extends BaseRecipe {
    protected SmeltingRecipe(String id) {
        super(id);
    }

    public void setInput(ItemDescriptor item) {
        this.ingredients.set(0, item);
    }

    public ItemDescriptor getInput() {
        return this.ingredients.getFirst();
    }

    public Item getResult() {
        return this.results.getFirst();
    }
}
