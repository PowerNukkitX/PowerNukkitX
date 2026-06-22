package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.RecipeRegistry;
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.CraftingDataEntryType;
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.RecipeUnlockingRequirement;

import java.util.Collections;
import java.util.UUID;

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

    public org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.ShapelessRecipe toNetwork() {
        return org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.ShapelessRecipe.of(
                CraftingDataEntryType.SHAPELESS_RECIPE,
                this.getRecipeId(),
                Collections.singletonList(this.getInput().toNetwork()),
                Collections.singletonList(this.getResult().toNetwork()),
                UUID.randomUUID(),
                this.getRecipeIdTag(),
                0,
                RecipeRegistry.FURNACE_RECIPE_NET_ID_COUNTER++,
                RecipeUnlockingRequirement.INVALID
        );
    }

    public abstract String getRecipeIdTag();
}
