package cn.nukkit.entity;

import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.level.Dimension;

/**
 * This interface represents the monster entity of the undead class
 *
 * @author MagicDroidX (Nukkit Project)
 */
public interface EntitySmite {
    default void burn(Entity entity) {
        boolean noHelmet = true;

        if (entity instanceof EntityInventoryHolder holder) {
            var armor = holder.getArmorInventory();
            noHelmet = armor == null || armor.getHelmet().isNull();
        }

        if (entity.getLevel().getDimension() == Dimension.DIMENSION_OVERWORLD
                && entity.getLevel().isDaytime()
                && !entity.getLevel().isRaining()
                && !entity.hasEffect(EffectType.FIRE_RESISTANCE)
                && noHelmet
                && !entity.isInsideOfWater()
                && !entity.isUnderBlock()
                && !entity.isOnFire()) {
            entity.setOnFire(1);
        }
    }
}