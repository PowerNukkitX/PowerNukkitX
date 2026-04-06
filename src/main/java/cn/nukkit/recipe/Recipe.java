package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface Recipe {
    @NotNull String getRecipeId();

    List<Item> getResults();

    List<ItemDescriptor> getIngredients();

    boolean match(Input input);

    default boolean fastCheck(Item... items) {
        if (getIngredients().size() != items.length) return false;
        boolean[] used = new boolean[items.length];
        for (var ingredient : getIngredients()) {
            boolean found = false;
            for (int i = 0; i < items.length; i++) {
                if (!used[i] && ingredient.match(items[i])) {
                    used[i] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    RecipeType getType();
}
