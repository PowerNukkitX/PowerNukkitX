package cn.nukkit.recipe;

import cn.nukkit.potion.Potion;
import cn.nukkit.registry.Registries;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RecipeTest {
    @BeforeAll
    @SneakyThrows
    static void before() {
        Potion.init();
        Registries.BLOCKSTATE_ITEMMETA.init();
        Registries.BLOCK.init();
        Registries.ITEM.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.RECIPE.init();
    }

    @Test
    void test_getNetworkIdRecipeList() {
        System.out.println(Registries.RECIPE.getNetworkIdRecipeList());
    }
}
