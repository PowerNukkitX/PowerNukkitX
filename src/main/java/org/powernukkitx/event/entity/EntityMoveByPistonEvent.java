package org.powernukkitx.event.entity;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.math.Vector3;

public class EntityMoveByPistonEvent extends EntityMotionEvent {
    public EntityMoveByPistonEvent(Entity entity, Vector3 pos) {
        super(entity, pos);
    }
}
