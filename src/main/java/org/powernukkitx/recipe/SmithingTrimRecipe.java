package org.powernukkitx.recipe;

import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeNetId;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.SmithingTrimRecipePayload;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;

/**
 * The type Smithing recipe for trim equipment.
 *
 * @author CoolLoong
 */
public class SmithingTrimRecipe extends BaseRecipe {
    private final String tag;
    @Getter
    private final int netId;

    public SmithingTrimRecipe(String id, int netId, ItemDescriptor base, ItemDescriptor addition, ItemDescriptor template, String tag) {
        super(id);
        this.netId = netId;
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

    public SmithingTrimRecipePayload toNetwork() {
        final SmithingTrimRecipePayload payload = new SmithingTrimRecipePayload();
        payload.setRecipeId(this.getRecipeId());
        payload.setTemplateIngredient(this.getIngredients().getFirst().toNetwork());
        payload.setBaseIngredient(this.getIngredients().get(1).toNetwork());
        payload.setAdditionIngredient(this.getIngredients().getLast().toNetwork());
        payload.setTag("smithing_table");
        payload.setNetId(new RecipeNetId(this.netId));
        return payload;
    }
}
