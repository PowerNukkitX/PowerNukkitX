package cn.nukkit.entity;

import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.level.Level;

/**
 * This interface represents the monster entity of the undead class
 *
 * @author MagicDroidX (Nukkit Project)
 */
public interface EntitySmite {
    default void burn(Entity entity) {
        if (entity.getLevel().getDimension() == Level.DIMENSION_OVERWORLD
                && entity.getLevel().isDaytime()
                && !entity.getLevel().isRaining()
                && !entity.hasEffect(EffectType.FIRE_RESISTANCE)
                && (!(entity instanceof EntityInventoryHolder entityInventoryHolder) || entityInventoryHolder.getHelmet().isNull())
                && !entity.isInsideOfWater()
                && !entity.isUnderBlock()
                && !entity.isOnFire()) {
            entity.setOnFire(1);
        }
    }
}