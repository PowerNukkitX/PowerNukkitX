package org.powernukkitx.item.enchantment.loot;

import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.enchantment.EnchantmentType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentLootDigging extends EnchantmentLoot {
    public EnchantmentLootDigging() {
        super(Enchantment.ID_FORTUNE_DIGGING, "lootBonusDigger", Rarity.RARE, EnchantmentType.DIGGER);
    }
}
