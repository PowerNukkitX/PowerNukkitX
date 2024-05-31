package cn.nukkit.item.enchantment.loot;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EnchantmentLoot extends Enchantment {
    
    /**
     * @deprecated 
     */
    protected EnchantmentLoot(int id, String name, Rarity rarity, EnchantmentType type) {
        super(id, name, rarity, type);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 15 + (level - 1) * 9;
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

    @Override
    /**
     * @deprecated 
     */
    
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != Enchantment.ID_SILK_TOUCH;
    }
}
