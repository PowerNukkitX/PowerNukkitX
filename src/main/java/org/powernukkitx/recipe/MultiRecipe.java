package org.powernukkitx.recipe;

import lombok.ToString;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.MultiRecipePayload;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeNetId;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.command.selector.args.impl.R;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;

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

    public MultiRecipePayload toNetwork() {
        final MultiRecipePayload payload = new MultiRecipePayload();
        payload.setMultiRecipeUUID(this.id);
        payload.setNetId(new RecipeNetId(this.netId));
        return payload;
    }
}
