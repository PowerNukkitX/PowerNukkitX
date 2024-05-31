package cn.nukkit.item.enchantment.bow;

import cn.nukkit.item.enchantment.Enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentBowKnockback extends EnchantmentBow {
    /**
     * @deprecated 
     */
    
    public EnchantmentBowKnockback() {
        super(Enchantment.ID_BOW_KNOCKBACK, "arrowKnockback", Rarity.RARE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 12 + (level - 1) * 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 25;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 2;
    }

}
