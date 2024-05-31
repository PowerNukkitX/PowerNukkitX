package cn.nukkit.item.enchantment.bow;

import cn.nukkit.item.enchantment.Enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentBowFlame extends EnchantmentBow {
    /**
     * @deprecated 
     */
    
    public EnchantmentBowFlame() {
        super(Enchantment.ID_BOW_FLAME, "arrowFire", Rarity.RARE);
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
