package cn.nukkit.recipe;

import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.RecipeRegistry;

/**
 * The type Smithing recipe for trim equipment.
 *
 * @author CoolLoong
 */
public class SmithingTrimRecipe extends BaseRecipe {
    private final String tag;

    public SmithingTrimRecipe(String id, ItemDescriptor base, ItemDescriptor addition, ItemDescriptor template, String tag) {
        super(id);
        results.clear();
        ingredients.add(template);
        ingredients.add(base);
        ingredients.add(addition);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean match(Input input) {
        return false;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SMITHING_TRIM;
    }

    public org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.SmithingTrimRecipe toNetwork() {
        return org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.SmithingTrimRecipe.of(
                this.getRecipeId(),
                this.getIngredients().getFirst().toNetwork(),
                this.getIngredients().get(1).toNetwork(),
                this.getIngredients().getLast().toNetwork(),
                "smithing_table",
                RecipeRegistry.RECIPE_NET_ID_COUNTER++
        );
    }
}