package org.powernukkitx.event.entity;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityDamageByBlockEvent extends EntityDamageEvent {
    private final Block damager;

    public EntityDamageByBlockEvent(Block damager, Entity entity, DamageCause cause, float damage) {
        super(entity, cause, damage);
        this.damager = damager;
    }

    public Block getDamager() {
        return damager;
    }

}
