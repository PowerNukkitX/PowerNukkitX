package cn.nukkit.item.randomitem;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author joserobjr
 * @since 2021-12-19
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class FishingTest {

    @Test
    void getFishingResult() {
        assertNotNull(Fishing.getFishingResult(null));

        Item fishingRod = Item.get(ItemID.FISHING_ROD);
        assertNotNull(Fishing.getFishingResult(fishingRod));

        fishingRod.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_LURE));
        assertNotNull(Fishing.getFishingResult(fishingRod));

        fishingRod.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_FORTUNE_FISHING));
        assertNotNull(Fishing.getFishingResult(fishingRod));
    }
}
