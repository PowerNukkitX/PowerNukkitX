package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.math.Vector3;

@PowerNukkitOnly
public class EntityMoveByPistonEvent extends EntityMotionEvent implements Cancellable {
    @PowerNukkitOnly
    public EntityMoveByPistonEvent(Entity entity, Vector3 pos) {
        super(entity, pos);
    }
}
