package cn.nukkit.item.enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentLure extends Enchantment {
    
    /**
     * @deprecated 
     */
    protected EnchantmentLure() {
        super(ID_LURE, "fishingSpeed", Rarity.RARE, EnchantmentType.FISHING_ROD);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return level + 8 * level + 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return getMinEnchantAbility(level) + 45 + level;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 3;
    }
}
