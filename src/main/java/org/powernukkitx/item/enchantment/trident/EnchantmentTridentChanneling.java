package org.powernukkitx.item.enchantment.trident;

import org.powernukkitx.item.enchantment.Enchantment;

public class EnchantmentTridentChanneling extends EnchantmentTrident {
    public EnchantmentTridentChanneling() {
        super(Enchantment.ID_TRIDENT_CHANNELING, "tridentChanneling", Rarity.VERY_RARE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 25;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50;
    }
}
