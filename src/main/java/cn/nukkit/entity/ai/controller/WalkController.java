package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MoveDirectionMemory;
import cn.nukkit.entity.ai.memory.MoveTargetMemory;
import cn.nukkit.entity.ai.memory.NeedUpdateMoveDirectionMemory;
import cn.nukkit.math.Vector3;

import java.util.Arrays;

/**
 * 处理陆地行走实体运动
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class WalkController implements IController {

    protected static final int JUMP_COOL_DOWN = 10;

    protected int currentJumpCoolDown = 0;

    @Override
    public boolean control(EntityIntelligent entity) {
        currentJumpCoolDown++;
        if (entity.getMemoryStorage().contains(MoveDirectionMemory.class) && !entity.getMemoryStorage().contains(NeedUpdateMoveDirectionMemory.class)) {
            Vector3 target = entity.getMemoryStorage().get(MoveTargetMemory.class).getData();
            MoveDirectionMemory directionMemory = entity.getMemoryStorage().get(MoveDirectionMemory.class);
            Vector3 direction = directionMemory.getEnd();
            var speed = entity.getMovementSpeedAtBlock(entity.getTickCachedLevelBlock());
            if (entity.motionX * entity.motionX + entity.motionZ * entity.motionZ > speed * speed * 0.4756) {
                return false;
            }
            var relativeVector = direction.clone().setComponents(direction.x - entity.x,
                    direction.y - entity.y, direction.z - entity.z);
            var xzLength = Math.sqrt(relativeVector.x * relativeVector.x + relativeVector.z * relativeVector.z);
            var k = speed / xzLength * 0.33;
            var dx = relativeVector.x * k;
            var dz = relativeVector.z * k;
            var dy = 0.0d;
            if (direction.y > entity.y && collidesBlocks(entity, dx, 0, dz) && currentJumpCoolDown > JUMP_COOL_DOWN) {
                if (entity.isOnGround() || entity.isTouchingWater()) {
                    //note: 从对BDS的抓包信息来看，台阶的碰撞箱在服务端和半砖一样，高度都为0.5
                    Block[] collisionBlocks = entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), false, false, Block::isSolid);
                    //计算出需要向上移动的高度
                    double maxY = Arrays.stream(collisionBlocks).map(b -> b.getCollisionBoundingBox().getMaxY()).max(Double::compareTo).orElse(0.0d);
                    //如果不跳就上不去，则跳跃
                    if (maxY - entity.getY() > entity.getFootHeight()) {
                        //TODO: 2022/7/15 按理说不需要这么做，但是不这么做就会出现实体上台阶跳的很高的问题
                        //有时我们并不需要跳那么高，所以说只跳需要跳的高度
                        dy += Math.min(maxY - entity.getY(), entity.getJumpingHeight()) * 0.43;
                        currentJumpCoolDown = 0;
                    }
                }
            }
            entity.addTmpMoveMotion(new Vector3(dx, dy, dz));
            if (xzLength < speed) {
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
