package org.powernukkitx.recipe;

import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.RecipeUnlockingRequirement;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.powernukkitx.recipe.RecipeType.USER_DATA_SHAPELESS_RECIPE;

/**
 * @author joserobjr
 * @since 2021-09-25
 */
public class UserDataShapelessRecipe extends ShapelessRecipe {
    public UserDataShapelessRecipe(Item result, Collection<Item> ingredients, int netId) {
        this(null, netId, 10, result, ingredients);
    }

    public UserDataShapelessRecipe(String recipeId, int netId, int priority, Item result, Collection<Item> ingredients) {
        this(recipeId, netId, priority, result, ingredients.stream().map(item -> (ItemDescriptor) new DefaultDescriptor(item)).toList());
    }

    public UserDataShapelessRecipe(String recipeId, int netId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, null, netId, priority, result, ingredients);
    }

    public UserDataShapelessRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, uuid, netId, priority, result, ingredients, null);
    }

    public UserDataShapelessRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, List<ItemDescriptor> ingredients, RecipeUnlockingRequirement recipeUnlockingRequirement) {
        super(recipeId == null ? RecipeRegistry.computeRecipeId(List.of(result), ingredients, USER_DATA_SHAPELESS_RECIPE) : recipeId, uuid, netId, priority, result, ingredients, recipeUnlockingRequirement);
    }

    @Override
    public RecipeType getType() {
        return USER_DATA_SHAPELESS_RECIPE;
    }
}
