package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.HandlerList;
import cn.nukkit.math.Vector3;

public class EntityMoveByPistonEvent extends EntityMotionEvent {
    public EntityMoveByPistonEvent(Entity entity, Vector3 pos) {
        super(entity, pos);
    }
}
