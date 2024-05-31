package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.ItemDescriptor;

public abstract class SmeltingRecipe extends BaseRecipe {
    
    /**
     * @deprecated 
     */
    protected SmeltingRecipe(String id) {
        super(id);
    }
    /**
     * @deprecated 
     */
    

    public void setInput(ItemDescriptor item) {
        this.ingredients.set(0, item);
    }

    public ItemDescriptor getInput() {
        return this.ingredients.get(0);
    }

    public Item getResult() {
        return this.results.get(0);
    }
}
