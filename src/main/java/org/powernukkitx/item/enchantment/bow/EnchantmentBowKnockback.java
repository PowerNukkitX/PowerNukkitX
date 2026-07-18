package org.powernukkitx.item.enchantment.bow;

import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentBowKnockback extends EnchantmentBow {
    public EnchantmentBowKnockback() {
        super(Enchantment.ID_BOW_KNOCKBACK, "arrowKnockback", Rarity.RARE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 12 + (level - 1) * 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 25;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public void doAttack(EntityDamageByEntityEvent event) {
        event.setKnockBack(event.getKnockBack() + this.getLevel() * 0.5f);
    }
}
