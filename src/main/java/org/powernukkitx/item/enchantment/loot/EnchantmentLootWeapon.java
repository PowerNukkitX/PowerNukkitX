package org.powernukkitx.item.enchantment.loot;

import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.enchantment.EnchantmentType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentLootWeapon extends EnchantmentLoot {

    public EnchantmentLootWeapon() {
        super(Enchantment.ID_LOOTING, "lootBonus", Rarity.RARE, EnchantmentType.SWORD);
    }

}
