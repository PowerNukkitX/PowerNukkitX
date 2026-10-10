package org.powernukkitx.inventory.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class CraftRecipeActionProcessorTest {

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @Test
    void computesRepairResultFromTwoDamagedItems() {
        Item first = Item.get(ItemID.IRON_SHOVEL, 200, 1).setCustomName("First shovel");
        Item second = Item.get(ItemID.IRON_SHOVEL, 225, 1);

        Item result = CraftRecipeActionProcessor.computeRepairRecipeResult(new Item[][]{
                {first, Item.AIR},
                {Item.AIR, second}
        });

        assertEquals(ItemID.IRON_SHOVEL, result.getId());
        assertEquals(1, result.getCount());
        assertEquals(163, result.getDamage());
        assertFalse(result.hasCustomName());
    }

    @Test
    void rejectsInputsThatDoNotMatchRepairRecipe() {
        Item shovel = Item.get(ItemID.IRON_SHOVEL, 200, 1);
        Item stackedShovels = Item.get(ItemID.IRON_SHOVEL, 225, 2);
        Item pickaxe = Item.get(ItemID.IRON_PICKAXE, 225, 1);

        assertNull(CraftRecipeActionProcessor.computeRepairRecipeResult(new Item[][]{{shovel}}));
        assertNull(CraftRecipeActionProcessor.computeRepairRecipeResult(new Item[][]{{shovel, stackedShovels}}));
        assertNull(CraftRecipeActionProcessor.computeRepairRecipeResult(new Item[][]{{shovel, pickaxe}}));
    }
}
