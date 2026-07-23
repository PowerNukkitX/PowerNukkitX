package org.powernukkitx.recipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RecipeTypeTest {

    @Test
    void valueOf_roundTripsForEveryConstant() {
        for (RecipeType type : RecipeType.values()) {
            Assertions.assertSame(type, RecipeType.valueOf(type.name()));
        }
    }

    @Test
    void networkType_matchesKnownValues() {
        Assertions.assertEquals(0, RecipeType.SHAPELESS.networkType);
        Assertions.assertEquals(1, RecipeType.SHAPED.networkType);
        Assertions.assertEquals(4, RecipeType.MULTI.networkType);
        Assertions.assertEquals(5, RecipeType.USER_DATA_SHAPELESS_RECIPE.networkType);
        Assertions.assertEquals(8, RecipeType.SMITHING_TRANSFORM.networkType);
        Assertions.assertEquals(9, RecipeType.SMITHING_TRIM.networkType);
        Assertions.assertEquals(-1, RecipeType.REPAIR.networkType);
    }
}
