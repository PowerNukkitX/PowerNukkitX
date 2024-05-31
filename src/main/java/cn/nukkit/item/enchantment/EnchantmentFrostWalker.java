package cn.nukkit.item.enchantment;

public class EnchantmentFrostWalker extends Enchantment {
    
    /**
     * @deprecated 
     */
    protected EnchantmentFrostWalker() {
        super(ID_FROST_WALKER, "frostwalker", Rarity.VERY_RARE, EnchantmentType.ARMOR_FEET);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return level * 10;
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
        return 2;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != ID_WATER_WALKER;
    }
}
