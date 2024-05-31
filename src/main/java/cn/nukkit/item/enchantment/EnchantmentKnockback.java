package cn.nukkit.item.enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentKnockback extends Enchantment {
    
    /**
     * @deprecated 
     */
    protected EnchantmentKnockback() {
        super(ID_KNOCKBACK, "knockback", Rarity.UNCOMMON, EnchantmentType.SWORD);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return super.getMinEnchantAbility(level) + 50;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxLevel() {
        return 2;
    }
}
