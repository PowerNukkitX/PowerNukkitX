package cn.nukkit.item.enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentWaterBreath extends Enchantment {
    
    /**
     * @deprecated 
     */
    protected EnchantmentWaterBreath() {
        super(ID_WATER_BREATHING, "oxygen", Rarity.RARE, EnchantmentType.ARMOR_HEAD);
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
        return this.getMinEnchantAbility(level) + 30;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 3;
    }
}
