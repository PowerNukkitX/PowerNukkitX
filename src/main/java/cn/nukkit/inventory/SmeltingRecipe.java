package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

@PowerNukkitOnly
public abstract class SmeltingRecipe implements Recipe {
    protected final Item output;

    protected Item ingredient;
    protected String craftingTag;

    @PowerNukkitOnly
    public SmeltingRecipe(Item result, Item ingredient) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
        this.craftingTag = null;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    public SmeltingRecipe(Item result, Item ingredient, String craftingTag) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
        this.craftingTag = craftingTag;
    }

    @PowerNukkitOnly
    public void setInput(Item item) {
        this.ingredient = item.clone();
    }

    @PowerNukkitOnly
    public Item getInput() {
        return this.ingredient.clone();
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }
}
