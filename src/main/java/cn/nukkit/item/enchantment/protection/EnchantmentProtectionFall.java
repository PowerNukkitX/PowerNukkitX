package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentProtectionFall extends EnchantmentProtection {
    /**
     * @deprecated 
     */
    

    public EnchantmentProtectionFall() {
        super(ID_PROTECTION_FALL, "fall", Rarity.UNCOMMON, TYPE.FALL);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getTypeModifier() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getProtectionFactor(EntityDamageEvent e) {
        DamageCause $1 = e.getCause();

        if (level <= 0 || (cause != DamageCause.FALL)) {
            return 0;
        }

        return (float) (getLevel() * getTypeModifier());
    }
}
