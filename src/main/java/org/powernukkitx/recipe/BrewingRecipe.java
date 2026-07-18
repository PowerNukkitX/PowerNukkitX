package org.powernukkitx.recipe;


import org.cloudburstmc.protocol.bedrock.data.payload.crafting.PotionMixDataEntry;
import org.powernukkitx.item.Item;
import org.powernukkitx.registry.RecipeRegistry;

import java.util.List;


public class BrewingRecipe extends MixRecipe {

    public BrewingRecipe(Item input, Item ingredient, Item output) {
        this(null, input, ingredient, output);
    }

    public BrewingRecipe(String recipeId, Item input, Item ingredient, Item output) {
        super(recipeId == null ? RecipeRegistry.computeRecipeIdWithItem(List.of(output), List.of(input, ingredient), RecipeType.BREWING) : recipeId, input, ingredient, output);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.BREWING;
    }

    public PotionMixDataEntry toNetwork() {
        final PotionMixDataEntry entry = new PotionMixDataEntry();
        entry.setFromPotionId(this.getInput().getRuntimeId());
        entry.setFromItemAux(this.getInput().getDamage());
        entry.setReagentItemId(this.getIngredient().getRuntimeId());
        entry.setReagentItemAux(this.getIngredient().getDamage());
        entry.setToPotionId(this.getResult().getRuntimeId());
        entry.setToItemAux(this.getResult().getDamage());
        return entry;
    }
}
