package org.powernukkitx.recipe.special;

import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.MultiRecipe;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.recipe.descriptor.ItemTagDescriptor;

import java.util.List;
import java.util.UUID;

/**
 * @author Buddelbubi
 * @see <a href="https://github.com/KoshakMineDEV/Lumi/blob/master/src/main/java/org/powernukkitx/recipe/impl/special/DecoratedPotRecipe.java">...</a>
 * @since 2025/11/19
 */
public class DecoratedPotRecipe extends MultiRecipe {

    public static final UUID RECIPE_UUID = UUID.fromString("685a742a-c42e-4a4e-88ea-5eb83fc98e5b");

    public DecoratedPotRecipe(int netId) {
        super(RECIPE_UUID, netId);
    }

    @Override
    public List<ItemDescriptor> getIngredients() {
        ItemTagDescriptor descriptor = new ItemTagDescriptor("minecraft:decorated_pot_sherds", 1);
        return List.of(descriptor);
    }


    @Override
    public List<Item> getResults() {
        return List.of(Item.get(Block.DECORATED_POT));
    }
}
