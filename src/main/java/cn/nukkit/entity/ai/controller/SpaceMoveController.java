package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MoveDirectionMemory;
import cn.nukkit.entity.ai.memory.MoveTargetMemory;
import cn.nukkit.entity.ai.memory.NeedUpdateMoveDirectionMemory;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BVector3;

/**
 * 处理3D实体运动
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SpaceMoveController implements IController {
    @Override
    public boolean control(EntityIntelligent entity) {
        if (entity.getMemoryStorage().contains(MoveDirectionMemory.class) && !entity.getMemoryStorage().contains(NeedUpdateMoveDirectionMemory.class)) {
            MoveDirectionMemory directionMemory = entity.getMemoryStorage().get(MoveDirectionMemory.class);
            Vector3 direction = directionMemory.getEnd();
            var speed = entity.getMovementSpeedAtBlock(entity.getTickCachedLevelBlock());
            if (entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ > speed * speed * 0.4756) {
                return false;
            }
            var relativeVector = direction.clone().setComponents(direction.x - entity.x,
                    direction.y - entity.y, direction.z - entity.z);
            var xyzLength = Math.sqrt(relativeVector.x * relativeVector.x + relativeVector.y * relativeVector.y + relativeVector.z * relativeVector.z);
            var k = speed / xyzLength * 0.33;
            var dx = relativeVector.x * k;
            var dy = relativeVector.y * k;
            var dz = relativeVector.z * k;
            entity.addTmpMoveMotion(new Vector3(dx, dy, dz));
            if (xyzLength < speed) {
                needNewDirection(entity);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    protected void needNewDirection(EntityIntelligent entity) {
        //通知需要新的移动目标
        entity.getMemoryStorage().put(new NeedUpdateMoveDirectionMemory(true));
    }

    protected boolean collidesBlocks(EntityIntelligent entity, double dx, double dy, double dz) {
        return entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), true,
                false, Block::isSolid).length > 0;
    }
}
