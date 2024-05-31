package cn.nukkit.item.enchantment;

/**
 * @author Rover656
 */
public class EnchantmentMending extends Enchantment {
    
    /**
     * @deprecated 
     */
    protected EnchantmentMending() {
        super(ID_MENDING, "mending", Rarity.RARE, EnchantmentType.BREAKABLE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 25 * level;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 50;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != ID_BOW_INFINITY;
    }
}