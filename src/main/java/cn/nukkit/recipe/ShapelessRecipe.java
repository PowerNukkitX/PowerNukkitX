package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.RecipeUnlockingRequirement;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.RecipeRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ShapelessRecipe extends CraftingRecipe {
    public ShapelessRecipe(Item result, Collection<Item> ingredients) {
        this(null, 10, result, ingredients);
    }

    public ShapelessRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
        this(recipeId, priority, result, ingredients.stream().map(item -> (ItemDescriptor) new DefaultDescriptor(item)).toList());
    }

    public ShapelessRecipe(String recipeId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, null, priority, result, ingredients);
    }

    public ShapelessRecipe(String recipeId, UUID uuid, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, uuid, priority, result, ingredients, null);
    }

    public ShapelessRecipe(String recipeId, UUID uuid, int priority, Item result, List<ItemDescriptor> ingredients, RecipeUnlockingRequirement recipeUnlockingRequirement) {
        super(recipeId == null ? RecipeRegistry.computeRecipeId(List.of(result), ingredients, RecipeType.SHAPELESS) : recipeId, priority, recipeUnlockingRequirement);
        this.uuid = uuid;
        this.results.add(result.clone());
        if (ingredients.size() > 9) {
            throw new IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients");
        }
        this.ingredients.addAll(ingredients);
    }

    public Item getResult() {
        return results.get(0);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS;
    }

    @Override
    public boolean match(Input input) {
        Item[][] data = input.getData();
        List<Item> flatInputItem = new ArrayList<>();
        for (int i = 0; i < input.getRow(); i++) {
            for (int j = 0; j < input.getCol(); j++) {
                if (!data[j][i].isNull()) {
                    flatInputItem.add(data[j][i]);
                }
            }
        }
        next:
        for (var i : flatInputItem) {
            for (var ingredient : ingredients) {
                if (ingredient.match(i)) continue next;
            }
            return false;
        }
        return true;
    }
}
