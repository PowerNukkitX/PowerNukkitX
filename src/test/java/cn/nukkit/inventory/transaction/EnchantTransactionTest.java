package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author joserobjr
 * @since 2021-12-19
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class EnchantTransactionTest {
    @MockPlayer
    Player player;

    EnchantTransaction transaction;

    @Test
    void getterSetter() {
        transaction = new EnchantTransaction(player, new ArrayList<>());
        Item input = Item.get(ItemID.IRON_SWORD);
        transaction.setInputItem(input);
        assertSame(input, transaction.getInputItem());

        Item output = input.clone();
        output.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DAMAGE_SMITE));
        transaction.setOutputItem(output);
        assertSame(output, transaction.getOutputItem());

        transaction.setCost(2);
        assertEquals(2, transaction.getCost());
    }
}
