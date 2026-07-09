package org.powernukkitx.item.enchantment.trident;

import org.powernukkitx.item.enchantment.Enchantment;

public class EnchantmentTridentRiptide extends EnchantmentTrident {
    public EnchantmentTridentRiptide() {
        super(Enchantment.ID_TRIDENT_RIPTIDE, "tridentRiptide", Rarity.RARE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 7 * level + 10;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) 
                && enchantment.id != Enchantment.ID_TRIDENT_LOYALTY
                && enchantment.id != Enchantment.ID_TRIDENT_CHANNELING;
    }
}
