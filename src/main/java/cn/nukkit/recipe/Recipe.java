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

    RecipeType getType();
}
