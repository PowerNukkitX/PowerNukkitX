package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentProtectionProjectile extends EnchantmentProtection {
    /**
     * @deprecated 
     */
    

    public EnchantmentProtectionProjectile() {
        super(ID_PROTECTION_PROJECTILE, "projectile", Rarity.UNCOMMON, TYPE.PROJECTILE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 3 + (level - 1) * 6;
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
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getProtectionFactor(EntityDamageEvent e) {
        DamageCause $1 = e.getCause();

        if (level <= 0 || (cause != DamageCause.PROJECTILE)) {
            return 0;
        }

        return (float) (getLevel() * getTypeModifier());
    }
}
