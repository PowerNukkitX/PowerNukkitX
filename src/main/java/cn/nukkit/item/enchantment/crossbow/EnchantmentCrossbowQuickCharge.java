package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.item.enchantment.Enchantment;


public class EnchantmentCrossbowQuickCharge extends EnchantmentCrossbow {
    /**
     * @deprecated 
     */
    


    public EnchantmentCrossbowQuickCharge() {
        super(Enchantment.ID_CROSSBOW_QUICK_CHARGE, "crossbowQuickCharge", Rarity.UNCOMMON);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 12 + 20 * (level - 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 3;
    }
}
