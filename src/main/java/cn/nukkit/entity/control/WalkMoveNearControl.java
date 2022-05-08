package cn.nukkit.entity.control;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;

public class WalkMoveNearControl implements Control {
    private final EntityIntelligent entity;

    public WalkMoveNearControl(EntityIntelligent entity) {
        this.entity = entity;
    }

    @Override
    public void control(int currentTick) {
        var vector = entity.movingNearDestination;
        if (vector != null) {
            vector = vector.clone().setComponents(vector.x - entity.x - entity.motionX,
                    vector.y - entity.y - entity.motionY, vector.z - entity.z - entity.motionZ);
            var xzLength = Math.sqrt(vector.x * vector.x + vector.z * vector.z);
            if (xzLength < entity.getMovementSpeed()) {
                entity.movingNearDestination = null;
                return;
            }
            var k = entity.getMovementSpeed() / xzLength * 0.33;
            var dx = vector.x * k;
            var dz = vector.z * k;
            entity.motionX += dx;
            entity.motionZ += dz;
            if (collidesBlocks(dx, 0, dz) && !collidesBlocks(dx, entity.getJumpingHeight(), dz)) {
                entity.jump();
            }
        }
    }

    private boolean collidesBlocks(double dx, double dy, double dz) {
        return entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), true,
                false, Block::isSolid).length > 0;
    }
}
