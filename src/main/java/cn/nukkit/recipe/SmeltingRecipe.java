package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.CraftingDataEntryType;

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

    public org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.FurnaceRecipe toNetwork() {
        return org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.FurnaceRecipe.of(
                CraftingDataEntryType.FURNACE_RECIPE,
                this.getInput().toItem().getRuntimeId(),
                this.getInput().toItem().getDamage(),
                this.getResult().toNetwork(),
                this.getRecipeIdTag()
        );
    }

    public abstract String getRecipeIdTag();
}
