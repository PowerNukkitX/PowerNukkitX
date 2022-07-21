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
 * 处理飞行实体运动
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SpaceMoveController implements IController {
    @Override
    public boolean control(EntityIntelligent entity) {
        if (entity.getMemoryStorage().contains(MoveDirectionMemory.class) && !entity.getMemoryStorage().contains(NeedUpdateMoveDirectionMemory.class)) {
            Vector3 target = entity.getMemoryStorage().get(MoveTargetMemory.class).getData();
            MoveDirectionMemory directionMemory = entity.getMemoryStorage().get(MoveDirectionMemory.class);
            Vector3 direction = directionMemory.getEnd();
            setYawAndPitch(entity, target, directionMemory);
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
            needNewDirection(entity);
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

    protected void setYawAndPitch(EntityIntelligent entity, Vector3 target, MoveDirectionMemory directionMemory) {
        //构建方向向量
        //先设置方向向量，以免出现到达目的地时生物不朝向目标的问题
        BVector3 bv2route = BVector3.fromPos(directionMemory.getEnd().x - entity.x, directionMemory.getEnd().y - entity.y, directionMemory.getEnd().z - entity.z);
        entity.setYaw(bv2route.getYaw());

        //构建指向玩家的向量
        BVector3 bv2player = BVector3.fromPos(target.x - entity.x, target.y - entity.y, target.z - entity.z);
        entity.setPitch(bv2player.getPitch());
        entity.setHeadYaw(bv2player.getHeadYaw());
    }
}
