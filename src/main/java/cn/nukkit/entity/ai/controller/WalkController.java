package cn.nukkit.entity.ai.controller;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCarpet;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityPhysical;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.math.Vector3;

import java.util.Arrays;

/**
 * Handling land walking entity movement
 * todo: To be decoupled
 */


public class WalkController implements IController {

    protected static final int JUMP_COOL_DOWN = 10;

    protected int currentJumpCoolDown = 0;

    private boolean canJump(Block block) {
        if (block.isSolid()) return true;
        else if (block instanceof BlockCarpet) return true;
        else return block.getId() == BlockID.FLOWER_POT || block.getId() == BlockID.CAKE;
    }

    @Override
    public boolean control(EntityIntelligent entity) {

        currentJumpCoolDown++;

        if(currentJumpCoolDown > JUMP_COOL_DOWN && !entity.isOnGround() && !entity.isTouchingWater())  {
            return false;
        }

        if (entity.hasMoveDirection() && !entity.isShouldUpdateMoveDirection()) {
            // Clone prevents NPE caused by asynchronous
            Vector3 direction = entity.getMoveDirectionEnd().clone();
            var speed = entity.getMovementSpeed();
            if (entity.motionX * entity.motionX + entity.motionZ * entity.motionZ > speed * speed * 0.4756) {
                entity.setDataFlag(EntityFlag.MOVING, false);
                return false;
            }
            var relativeVector = direction.clone().setComponents(direction.x - entity.x,
                    direction.y - entity.y, direction.z - entity.z);
            var xzLengthSquared = relativeVector.x * relativeVector.x + relativeVector.z * relativeVector.z;
            if (Math.abs(xzLengthSquared) < EntityPhysical.PRECISION) {
                entity.setDataFlag(EntityFlag.MOVING, false);
                return false;
            }
            var xzLength = Math.sqrt(relativeVector.x * relativeVector.x + relativeVector.z * relativeVector.z);
            var k = speed / xzLength * 0.33;
            var dx = relativeVector.x * k;
            var dz = relativeVector.z * k;
            var dy = 0.0d;
            Block target = entity.getLevel().getBlock(entity.getMoveDirectionStart());
            if (target.down().isSolid() && relativeVector.y > 0 && collidesBlocks(entity, dx, 0, dz) && currentJumpCoolDown > JUMP_COOL_DOWN || (entity.isTouchingWater() && !(target instanceof BlockLiquid || target.getLevel().getBlock(target, 1) instanceof BlockLiquid)  && target.down().isSolid())) {
                // note: From the BDS packet capture information, the collision box of the stairs is the same as that of the half brick on the server side, and the height is 0.5
                Block[] collisionBlocks = entity.level.getTickCachedCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), false, false, this::canJump);
                // Calculate the height you need to move upward
                double maxY = Arrays.stream(collisionBlocks).map(b -> b.getCollisionBoundingBox().getMaxY()).max(Double::compareTo).orElse(0.0d);
                double diffY = maxY - entity.getY();
                if (diffY > 0.01 && diffY <= 1.1) { // 1.1 gives some leeway for stairs etc.
                    dy += entity.getJumpingMotion(diffY);
                    currentJumpCoolDown = 0;
                }
            }
            entity.addTmpMoveMotion(new Vector3(dx, dy, dz));
            entity.setDataFlag(EntityFlag.MOVING, true);
            if (xzLength < speed) {
                needNewDirection(entity);
                return false;
            }
            return true;
        } else {
            entity.setDataFlag(EntityFlag.MOVING, false);
            return false;
        }
    }

    protected void needNewDirection(EntityIntelligent entity) {
        // Notification requires new moving target
        entity.setShouldUpdateMoveDirection(true);
    }

    protected boolean collidesBlocks(EntityIntelligent entity, double dx, double dy, double dz) {
        return entity.level.getTickCachedCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), true,
                false, this::canJump).length > 0;
    }
}
