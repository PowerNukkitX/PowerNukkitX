package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentEfficiency extends Enchantment {
    
    /**
     * @deprecated 
     */
    protected EnchantmentEfficiency() {
        super(ID_EFFICIENCY, "digging", Rarity.COMMON, EnchantmentType.DIGGER);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 1 + (level - 1) * 10;
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
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canEnchant(Item item) {
        return item.isShears() || super.canEnchant(item);
    }

}
