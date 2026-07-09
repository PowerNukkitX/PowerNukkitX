package org.powernukkitx.item.enchantment.crossbow;

import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.enchantment.EnchantmentType;


public abstract class EnchantmentCrossbow extends Enchantment {


    protected EnchantmentCrossbow(int id, String name, Rarity rarity) {
        super(id, name, rarity, EnchantmentType.CROSSBOW);
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50;
    }
}
