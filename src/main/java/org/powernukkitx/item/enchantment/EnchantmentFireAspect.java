package org.powernukkitx.item.enchantment;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.entity.EntityCombustByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentFireAspect extends Enchantment {
    protected EnchantmentFireAspect() {
        super(ID_FIRE_ASPECT, "fire", Rarity.RARE, EnchantmentType.SWORD);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 + (level - 1) * 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return super.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public void doAttack(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity attacker = event.getDamager();
        if ((!(entity instanceof Player) || !((Player) entity).isCreative())) {
            int duration = Math.max(entity.fireTicks / 20, getLevel() << 2);

            EntityCombustByEntityEvent ev = new EntityCombustByEntityEvent(attacker, entity, duration);
            Server.getInstance().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                entity.setOnFire(ev.getDuration());
            }
        }
    }
}
