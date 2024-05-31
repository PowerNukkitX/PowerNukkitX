package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentProtectionFire extends EnchantmentProtection {
    /**
     * @deprecated 
     */
    

    public EnchantmentProtectionFire() {
        super(ID_PROTECTION_FIRE, "fire", Rarity.UNCOMMON, TYPE.FIRE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 10 + (level - 1) * 8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 8;
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

        if (level <= 0 || (cause != DamageCause.LAVA && cause != DamageCause.FIRE && cause != DamageCause.FIRE_TICK)) {
            return 0;
        }

        return (float) (getLevel() * getTypeModifier());
    }
}
