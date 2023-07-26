package cn.nukkit.entity;

import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.level.Level;
import cn.nukkit.potion.Effect;

/**
 * 这个接口代表亡灵类的怪物实体
 * <p>
 * This interface represents the monster entity of the undead class
 *
 * @author MagicDroidX (Nukkit Project)
 */
public interface EntitySmite {
    default void burn(Entity entity) {
        if (entity.getLevel().getDimension() == Level.DIMENSION_OVERWORLD
                && entity.getLevel().isDaytime()
                && (!entity.hasEffect(Effect.FIRE_RESISTANCE)
                        || (entity instanceof EntityInventoryHolder entityInventoryHolder
                                && entityInventoryHolder.getHelmet().isNull()))
                && !entity.isInsideOfWater()
                && !entity.isUnderBlock()
                && !entity.isOnFire()) {
            entity.setOnFire(1);
        }
    }
}
