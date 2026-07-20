package org.powernukkitx.recipe;

import org.cloudburstmc.protocol.bedrock.data.payload.crafting.ContainerMixDataEntry;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemPotion;
import org.powernukkitx.registry.RecipeRegistry;

import java.util.List;

public class ContainerRecipe extends MixRecipe {
    public ContainerRecipe(Item input, Item ingredient, Item output) {
        this(null, input, ingredient, output);
    }

    public ContainerRecipe(String recipeId, Item input, Item ingredient, Item output) {
        super(recipeId == null ? RecipeRegistry.computeRecipeIdWithItem(List.of(output), List.of(input, ingredient), RecipeType.CONTAINER) : recipeId,
            input, ingredient, output);
    }

    @Override
    public boolean fastCheck(Item... items) {
        if (items.length == 2 && items[1] instanceof ItemPotion) {
            return items[0].equals(getIngredient());
        }
        return false;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.CONTAINER;
    }

    public ContainerMixDataEntry toNetwork() {
        final ContainerMixDataEntry entry = new ContainerMixDataEntry();
        entry.setFromItemId(this.getInput().getRuntimeId());
        entry.setReagentItemId(this.getIngredient().getRuntimeId());
        entry.setOutputItemId(this.getResult().getRuntimeId());
        return entry;
    }
}
