package org.powernukkitx.recipe;

import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;


@ToString
public class MultiRecipe implements Recipe {

    private final UUID id;
    private final int netId;

    public MultiRecipe(UUID id, int netId) {
        this.id = id;
        this.netId = netId;
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

    public int getNetId() {
        return netId;
    }

    public org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.MultiRecipe toNetwork() {
        return org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.MultiRecipe.of(
                this.id,
                this.netId
        );
    }
}
