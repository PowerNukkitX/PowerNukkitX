package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.RecipeRegistry;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;


@ToString
public class MultiRecipe implements Recipe {

    private final UUID id;

    public MultiRecipe(UUID id) {
        this.id = id;
    }

    @Override
    public @NotNull String getRecipeId() {
        return id.toString();
    }

    @Override
    public List<Item> getResults() {
        return List.of();
    }

    @Override
    public List<ItemDescriptor> getIngredients() {
        return List.of();
    }

    @Override
    public boolean match(Input input) {
        return true;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.MULTI;
    }

    public UUID getId() {
        return this.id;
    }

    public org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.MultiRecipe toNetwork() {
        return org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.MultiRecipe.of(
                this.id,
                RecipeRegistry.RECIPE_NET_ID_COUNTER++
        );
    }
}
