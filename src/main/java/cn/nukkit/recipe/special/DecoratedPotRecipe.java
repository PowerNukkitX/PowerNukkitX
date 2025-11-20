package cn.nukkit.recipe.special;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.recipe.MultiRecipe;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.recipe.descriptor.ItemTagDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Buddelbubi
 * @since 2025/11/19
 * @see <a href="https://github.com/KoshakMineDEV/Lumi/blob/master/src/main/java/cn/nukkit/recipe/impl/special/DecoratedPotRecipe.java">...</a>
 */
public class DecoratedPotRecipe extends MultiRecipe {

    public static final UUID RECIPE_UUID = UUID.fromString("685a742a-c42e-4a4e-88ea-5eb83fc98e5b");

    public DecoratedPotRecipe() {
        super(RECIPE_UUID);
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
