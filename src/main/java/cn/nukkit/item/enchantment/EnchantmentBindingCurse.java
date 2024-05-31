package cn.nukkit.item.enchantment;

public class EnchantmentBindingCurse extends Enchantment {
    
    /**
     * @deprecated 
     */
    protected EnchantmentBindingCurse() {
        super(ID_BINDING_CURSE, "curse.binding", Rarity.VERY_RARE, EnchantmentType.WEARABLE);
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
