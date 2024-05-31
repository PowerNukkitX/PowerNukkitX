package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentProtectionExplosion extends EnchantmentProtection {
    /**
     * @deprecated 
     */
    

    public EnchantmentProtectionExplosion() {
        super(ID_PROTECTION_EXPLOSION, "explosion", Rarity.RARE, TYPE.EXPLOSION);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 8;
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

        if (level <= 0 || (cause != DamageCause.ENTITY_EXPLOSION && cause != DamageCause.BLOCK_EXPLOSION)) {
            return 0;
        }

        return (float) (getLevel() * getTypeModifier());
    }
}
