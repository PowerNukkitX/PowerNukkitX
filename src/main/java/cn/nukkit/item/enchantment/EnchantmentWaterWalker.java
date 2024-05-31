package cn.nukkit.item.enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentWaterWalker extends Enchantment {
    
    /**
     * @deprecated 
     */
    protected EnchantmentWaterWalker() {
        super(ID_WATER_WALKER, "waterWalker", Rarity.RARE, EnchantmentType.ARMOR_FEET);
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
        return 3;
    }
}
