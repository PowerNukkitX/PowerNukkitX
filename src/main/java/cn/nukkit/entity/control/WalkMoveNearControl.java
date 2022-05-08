package cn.nukkit.entity.control;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;

public class WalkMoveNearControl implements Control<Vector3> {
    private final EntityIntelligent entity;

    public WalkMoveNearControl(EntityIntelligent entity) {
        this.entity = entity;
    }

    @Override
    public Vector3 control(int currentTick) {
        var vector = entity.movingNearDestination;
        if (vector != null) {
            var speed = entity.getMovementSpeed();
            if (entity.motionX * entity.motionX + entity.motionZ * entity.motionZ > speed * speed * 0.4356) {
                return Vector3.ZERO;
            }
            vector = vector.clone().setComponents(vector.x - entity.x,
                    vector.y - entity.y, vector.z - entity.z);
            var xzLength = Math.sqrt(vector.x * vector.x + vector.z * vector.z);
            if (xzLength < speed) {
                entity.movingNearDestination = null;
                return Vector3.ZERO;
            }
            var k = speed / xzLength * 0.33;
            var dx = vector.x * k;
            var dz = vector.z * k;
            if (collidesBlocks(dx, 0, dz) && !collidesBlocks(dx, entity.getJumpingHeight(), dz)) {
                entity.jump();
            }
            return new Vector3(dx, 0, dz);
        }
        return Vector3.ZERO;
    }

    private boolean collidesBlocks(double dx, double dy, double dz) {
        return entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), true,
                false, Block::isSolid).length > 0;
    }
}
