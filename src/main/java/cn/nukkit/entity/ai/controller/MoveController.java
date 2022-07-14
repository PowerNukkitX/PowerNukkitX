package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MoveDirectionMemory;
import cn.nukkit.entity.ai.memory.MoveTargetMemory;
import cn.nukkit.entity.ai.memory.NeedUpdateMoveDestinationMemory;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BVector3;

import java.util.Arrays;
import java.util.List;

/**
 * 处理实体运动请求
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class MoveController implements IController {

    protected static final int JUMP_COOL_DOWN = 10;

    protected int currentJumpCoolDown = 0;

    @Override
    public boolean control(EntityIntelligent entity) {
        currentJumpCoolDown++;
        if (entity.getMemoryStorage().contains(MoveDirectionMemory.class) && !entity.getMemoryStorage().contains(NeedUpdateMoveDestinationMemory.class)) {
            Vector3 target = (Vector3) entity.getMemoryStorage().get(MoveTargetMemory.class).getData();
            MoveDirectionMemory directionMemory = (MoveDirectionMemory) entity.getMemoryStorage().get(MoveDirectionMemory.class);
            Vector3 destination = directionMemory.getEnd();
            setYawAndPitch(entity, target, directionMemory);
            var speed = entity.getMovementSpeed();
            if (entity.motionX * entity.motionX + entity.motionZ * entity.motionZ > speed * speed * 0.4756) {
                return false;
            }
            var relativeVector = destination.clone().setComponents(destination.x - entity.x,
                    destination.y - entity.y, destination.z - entity.z);
            var xzLength = Math.sqrt(relativeVector.x * relativeVector.x + relativeVector.z * relativeVector.z);
            if (xzLength < speed) {
                needNewDestination(entity);
                return false;
            }
            var k = speed / xzLength * 0.33;
            var dx = relativeVector.x * k;
            var dz = relativeVector.z * k;
            var dy = 0.0d;
            //todo: 这里的跳跃实现有待改进
            if (destination.y > entity.y && collidesBlocks(entity, dx, 0, dz) && currentJumpCoolDown > JUMP_COOL_DOWN) {
                if (entity.isOnGround() || entity.isTouchingWater()) {
                    //todo: 检查是否需要跳跃（半砖、台阶等可以直接走过去的就不用了）
                    //note: 从对BDS的抓包信息来看，台阶的碰撞箱在服务端和半砖一样，高度都为0.5
                    Block[] collisionBlocks =  entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), false, false, Block::isSolid);
                    double maxY = Arrays.stream(collisionBlocks).map(b -> b.getCollisionBoundingBox().getMaxY()).max(Double::compareTo).orElse(0.0d);
                    if (maxY - entity.getY() > 0.5) {
                        dy += entity.getJumpingHeight() * 0.43;
                        currentJumpCoolDown = 0;
                    }
                }
            }
            entity.addTmpMoveMotion(new Vector3(dx, dy, dz));
            return true;
        } else {
            needNewDestination(entity);
            return false;
        }
    }

    protected void needNewDestination(EntityIntelligent entity) {
        //通知需要新的移动目标
        entity.getMemoryStorage().put(new NeedUpdateMoveDestinationMemory(true));
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
