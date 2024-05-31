package cn.nukkit.item.enchantment.trident;

import cn.nukkit.item.enchantment.Enchantment;

public class EnchantmentTridentChanneling extends EnchantmentTrident {
    /**
     * @deprecated 
     */
    
    public EnchantmentTridentChanneling() {
        super(Enchantment.ID_TRIDENT_CHANNELING, "tridentChanneling", Rarity.VERY_RARE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 25;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return 50;
    }
}
