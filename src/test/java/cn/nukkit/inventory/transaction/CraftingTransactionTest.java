package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.inventory.RepairRecipe;
import cn.nukkit.inventory.ShapelessRecipe;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author joserobjr
 * @since 2021-12-19
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class CraftingTransactionTest {
    @MockPlayer
    Player player;

    List<InventoryAction> actions;

    CraftingTransaction transaction;

    @BeforeEach
    void setUp() {
        actions = new ArrayList<>();
        transaction = new CraftingTransaction(player, actions);
    }

    @SuppressWarnings("deprecation")
    @Test
    void recipe() {
        Recipe recipe = new ShapelessRecipe(Item.get(ItemID.COAL), Collections.singletonList(Item.getBlock(BlockID.COAL_ORE)));
        transaction.setTransactionRecipe(recipe);
        assertSame(recipe, transaction.getTransactionRecipe());
        assertSame(recipe, transaction.getRecipe());
        assertSame(recipe, transaction.recipe);

        recipe = new RepairRecipe(InventoryType.GRINDSTONE, Item.get(ItemID.IRON_SWORD), Arrays.asList(Item.get(ItemID.IRON_SWORD, 33), Item.get(ItemID.IRON_SWORD, 12)));
        transaction.setTransactionRecipe(recipe);
        assertSame(recipe, transaction.getTransactionRecipe());
        assertNull(transaction.getRecipe());
        assertNull(transaction.recipe);
    }
}
