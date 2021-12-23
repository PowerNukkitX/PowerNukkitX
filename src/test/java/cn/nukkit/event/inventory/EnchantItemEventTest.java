package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.item.enchantment.Enchantment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-12-19
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class EnchantItemEventTest {
    @Mock
    EnchantInventory inventory;

    @MockPlayer
    Player player;

    EnchantItemEvent event;

    @Test
    void construction() {
        assertNotNull(EnchantItemEvent.getHandlers());

        Item before = Item.get(ItemID.IRON_SWORD);
        Item after = before.clone();
        after.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DAMAGE_SMITE));
        event = new EnchantItemEvent(inventory, before, after, 2, player);

        assertSame(before, event.getOldItem());
        assertSame(after, event.getNewItem());
        assertEquals(2, event.getXpCost());
        assertSame(player, event.getEnchanter());
    }

    @Test
    void setters() {
        Item before = Item.get(ItemID.IRON_SWORD);
        Item after = before.clone();
        after.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DAMAGE_SMITE));
        event = new EnchantItemEvent(inventory, before, after, 2, null);
        assertNull(event.getEnchanter());

        before = MinecraftItemID.IRON_AXE.get(1);
        after = before.clone();
        after.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DAMAGE_SMITE));

        event.setOldItem(before);
        event.setNewItem(after);
        event.setXpCost(3);
        event.setEnchanter(player);

        assertSame(before, event.getOldItem());
        assertSame(after, event.getNewItem());
        assertEquals(3, event.getXpCost());
        assertSame(player, event.getEnchanter());
    }
}
