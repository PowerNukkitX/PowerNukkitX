package cn.nukkit.recipe;


import cn.nukkit.item.Item;
import cn.nukkit.registry.RecipeRegistry;
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.PotionMixDataEntry;

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
        return new PotionMixDataEntry(
                this.getInput().getRuntimeId(),
                this.getInput().getDamage(),
                this.getIngredient().getRuntimeId(),
                this.getIngredient().getDamage(),
                this.getResult().getRuntimeId(),
                this.getResult().getDamage()
        );
    }
}