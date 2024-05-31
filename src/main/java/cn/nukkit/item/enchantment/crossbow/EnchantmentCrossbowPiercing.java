package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.item.enchantment.Enchantment;


public class EnchantmentCrossbowPiercing extends EnchantmentCrossbow {
    /**
     * @deprecated 
     */
    


    public EnchantmentCrossbowPiercing() {
        super(Enchantment.ID_CROSSBOW_PIERCING, "crossbowPiercing", Rarity.COMMON);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 1 + 10 * (level - 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != ID_CROSSBOW_MULTISHOT;
    }
}
