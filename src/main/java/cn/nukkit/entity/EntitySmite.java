package cn.nukkit.entity;

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
        if (entity.getLevel().getDimension() == Level.DIMENSION_OVERWORLD)
            if (entity.getLevel().isDaytime())
                if (!entity.hasEffect(Effect.FIRE_RESISTANCE))
                    if (!entity.isInsideOfWater())
                        if (!entity.isUnderBlock())
                            if (!entity.isOnFire())
                                entity.setOnFire(1);
    }
}
