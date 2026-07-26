package org.powernukkitx.recipe;

import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.registry.RecipeRegistry;
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
