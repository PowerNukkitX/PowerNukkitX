package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.RecipeRegistry;
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.RecipeUnlockingRequirement;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static cn.nukkit.recipe.RecipeType.CARTOGRAPHY;


public class CartographyRecipe extends ShapelessRecipe {

    public CartographyRecipe(Item result, int netId, Collection<Item> ingredients) {
        this(null, netId, 10, result, ingredients);
    }

    public CartographyRecipe(String recipeId, int netId, int priority, Item result, Collection<Item> ingredients) {
        this(recipeId, netId, priority, result, ingredients.stream().map(item -> (ItemDescriptor) new DefaultDescriptor(item)).toList());
    }

    public CartographyRecipe(String recipeId, int netId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, null, netId, priority, result, ingredients);
    }

    public CartographyRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, uuid, netId, priority, result, ingredients, null);
    }

    public CartographyRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, List<ItemDescriptor> ingredients, RecipeUnlockingRequirement recipeUnlockingRequirement) {
        super(recipeId == null ? RecipeRegistry.computeRecipeId(List.of(result), ingredients, CARTOGRAPHY) : recipeId, uuid, netId, priority, result, ingredients, recipeUnlockingRequirement);
    }

    @Override
    public RecipeType getType() {
        return CARTOGRAPHY;
    }

    @Override
    public String getRecipeIdTag() {
        return "cartography_table";
    }
}
