package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.registry.RecipeRegistry;
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.ContainerMixDataEntry;

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
        return new ContainerMixDataEntry(
                this.getInput().getRuntimeId(),
                this.getIngredient().getRuntimeId(),
                this.getResult().getRuntimeId()
        );
    }
}
