package cn.nukkit.recipe;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Potion;
import cn.nukkit.utils.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(PowerNukkitExtension.class)
public class RecipeParseTest {
    static final Config customRecipes;
    static final List<Map> recipes;
    static final CraftingManager craftingManager;

    static {
        Potion.init();
        Block.init();
        Item.init();
        try {
            var constructor = CraftingManager.class.getDeclaredConstructor(Boolean.class);
            constructor.setAccessible(true);
            craftingManager = constructor.newInstance(true);
            customRecipes = new Config(new File(RecipeParseTest.class.getClassLoader().getResource("cn\\nukkit\\test\\shapeRecipe.json").toURI()), Config.JSON);
            recipes = customRecipes.getMapList("recipes");
        } catch (URISyntaxException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shapeRecipe() {
        boolean result = true;
        toNextRecipe:
        for (Map<String, Object> recipe : recipes) {
            if (craftingManager.parseShapeRecipe(recipe).isEmpty()) result = false;
        }
        assertTrue(result);
    }
}
