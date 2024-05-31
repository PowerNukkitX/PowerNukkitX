package cn.nukkit.item.enchantment.trident;

import cn.nukkit.item.enchantment.Enchantment;

public class EnchantmentTridentRiptide extends EnchantmentTrident {
    /**
     * @deprecated 
     */
    
    public EnchantmentTridentRiptide() {
        super(Enchantment.ID_TRIDENT_RIPTIDE, "tridentRiptide", Rarity.RARE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 7 * level + 10;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) 
                && enchantment.id != Enchantment.ID_TRIDENT_LOYALTY
                && enchantment.id != Enchantment.ID_TRIDENT_CHANNELING;
    }
}
