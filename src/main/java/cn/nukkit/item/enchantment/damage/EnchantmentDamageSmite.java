package cn.nukkit.item.enchantment.damage;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntitySmite;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentDamageSmite extends EnchantmentDamage {
    /**
     * @deprecated 
     */
    

    public EnchantmentDamageSmite() {
        super(ID_DAMAGE_SMITE, "undead", Rarity.UNCOMMON, TYPE.SMITE);
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
        return this.getMinEnchantAbility(level) + 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getDamageBonus(Entity entity) {
        if (entity instanceof EntitySmite) {
            return getLevel() * 2.5;
        }

        return 0;
    }
}
