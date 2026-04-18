package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.registry.RecipeRegistry;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Collection;
import java.util.List;


public class RepairRecipe extends BaseRecipe {
    private final ContainerType inventoryType;

    public RepairRecipe(ContainerType inventoryType, Item result, Collection<Item> ingredients) {
        super(RecipeRegistry.computeRecipeIdWithItem(List.of(result), ingredients, RecipeType.REPAIR));
        this.inventoryType = inventoryType;
        this.results.add(result.clone());
        for (Item item : ingredients) {
            if (item.getCount() < 1) {
                throw new IllegalArgumentException("Recipe Ingredient amount was not 1 (value: " + item.getCount() + ")");
            }
            this.ingredients.add(new DefaultDescriptor(item.clone()));
        }
    }

    @Override
    public boolean match(Input input) {
        return true;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.REPAIR;
    }

    public ContainerType getInventoryType() {
        return inventoryType;
    }
}
