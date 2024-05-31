package cn.nukkit.item.enchantment.bow;

import cn.nukkit.item.enchantment.Enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentBowInfinity extends EnchantmentBow {
    /**
     * @deprecated 
     */
    
    public EnchantmentBowInfinity() {
        super(Enchantment.ID_BOW_INFINITY, "arrowInfinite", Rarity.VERY_RARE);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != Enchantment.ID_MENDING;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return 50;
    }
}
