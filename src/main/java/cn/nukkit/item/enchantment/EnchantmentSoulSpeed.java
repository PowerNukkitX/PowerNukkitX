package cn.nukkit.item.enchantment;


public class EnchantmentSoulSpeed extends Enchantment {


    
    /**
     * @deprecated 
     */
    protected EnchantmentSoulSpeed() {
        super(ID_SOUL_SPEED, "soul_speed", Rarity.VERY_RARE, EnchantmentType.ARMOR_FEET);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 10 * level;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return getMinEnchantAbility(level) + 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 3;
    }
}
