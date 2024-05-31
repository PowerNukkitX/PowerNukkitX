package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.enchantment.Enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentProtectionAll extends EnchantmentProtection {
    /**
     * @deprecated 
     */
    

    public EnchantmentProtectionAll() {
        super(Enchantment.ID_PROTECTION_ALL, "all", Rarity.COMMON, TYPE.ALL);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 1 + (level - 1) * 11;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 11;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getTypeModifier() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getProtectionFactor(EntityDamageEvent e) {
        DamageCause $1 = e.getCause();

        if (level <= 0 || cause == DamageCause.VOID || cause == DamageCause.CUSTOM || cause == DamageCause.MAGIC || cause == DamageCause.HUNGER) {
            return 0;
        }

        return (float) (getLevel() * getTypeModifier());
    }
}
