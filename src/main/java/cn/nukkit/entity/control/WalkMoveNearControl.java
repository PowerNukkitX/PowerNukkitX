package cn.nukkit.entity.control;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class WalkMoveNearControl implements Control<Vector3> {
    private final EntityIntelligent entity;
    private ControlState state = ControlState.NOT_WORKING;

    public WalkMoveNearControl(EntityIntelligent entity) {
        this.entity = entity;
    }

    @Override
    public Vector3 control(int currentTick, boolean needsRecalcMovement) {
        if (state == ControlState.JUST_DONE) {
            state = ControlState.NOT_WORKING;
        }
        if (!needsRecalcMovement) {
            return entity.previousMoveNearMotion;
        }
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
                state = ControlState.JUST_DONE;
                return Vector3.ZERO;
            }
            var k = speed / xzLength * 0.33;
            var dx = vector.x * k;
            var dz = vector.z * k;
            if (collidesBlocks(dx, 0, dz)) {
                if (entity.isFloating() && !collidesBlocks(dx, entity.getHeight(), dz)) {
                    entity.shore();
                } else if (!collidesBlocks(dx, entity.getJumpingHeight(), dz)) {
                    entity.jump();
                }
            }
            state = ControlState.WORKING;
            return new Vector3(dx, 0, dz);
        } else {
            state = ControlState.NOT_WORKING;
        }
        return Vector3.ZERO;
    }

    @NotNull
    @Override
    public ControlState getState() {
        return state;
    }

    private boolean collidesBlocks(double dx, double dy, double dz) {
        return entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), true,
                false, Block::isSolid).length > 0;
    }
}
