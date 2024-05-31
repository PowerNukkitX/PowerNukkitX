package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;


public abstract class EnchantmentCrossbow extends Enchantment {


    
    /**
     * @deprecated 
     */
    protected EnchantmentCrossbow(int id, String name, Rarity rarity) {
        super(id, name, rarity, EnchantmentType.CROSSBOW);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return 50;
    }
}
