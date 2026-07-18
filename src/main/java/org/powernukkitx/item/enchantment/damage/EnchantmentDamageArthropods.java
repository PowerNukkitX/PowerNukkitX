package org.powernukkitx.item.enchantment.damage;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityArthropod;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentDamageArthropods extends EnchantmentDamage {

    public EnchantmentDamageArthropods() {
        super(ID_DAMAGE_ARTHROPODS, "arthropods", Rarity.UNCOMMON, TYPE.SMITE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 20;
    }

    @Override
    public double getDamageBonus(Entity target, Entity damager) {
        if (target instanceof EntityArthropod) {
            return getLevel() * 2.5;
        }

        return 0;
    }

    @Override
    public void doAttack(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityArthropod) {
            int duration = 20 + ThreadLocalRandom.current().nextInt(10 * this.level);
            entity.addEffect(Effect.get(EffectType.SLOWNESS).setDuration(duration).setAmplifier(3));
        }
    }
}
