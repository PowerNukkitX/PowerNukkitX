package cn.nukkit.item.enchantment.bow;

import cn.nukkit.item.enchantment.Enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentBowPower extends EnchantmentBow {
    /**
     * @deprecated 
     */
    
    public EnchantmentBowPower() {
        super(Enchantment.ID_BOW_POWER, "arrowDamage", Rarity.COMMON);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 5;
    }
}
