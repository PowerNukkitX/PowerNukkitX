package org.powernukkitx.entity.ai.controller;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityPhysical;
import org.powernukkitx.entity.mob.EntitySlime;
import org.powernukkitx.entity.passive.EntityRabbit;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

import java.util.Arrays;

/**
 * Handles movement of land-walking entities
 * todo: needs to be decoupled
 */


public class HoppingController extends WalkController {

    protected int moveCooldown = 0;

    protected int currentJumpCoolDown = 0;

    public HoppingController(int moveCooldown) {
        this.moveCooldown = moveCooldown;
    }

    @Override
    public boolean control(EntityIntelligent entity) {
        currentJumpCoolDown++;
        if (entity.hasMoveDirection() && !entity.isShouldUpdateMoveDirection() && currentJumpCoolDown > moveCooldown) {
            //clone to prevent NPE caused by asynchronous access
            Vector3 direction = entity.getMoveDirectionEnd().clone();
            var speed = entity.getMovementSpeed();
            if (entity.motionX * entity.motionX + entity.motionZ * entity.motionZ > speed * speed * 0.4756) {
                entity.setDataFlag(ActorFlags.MOVING, false);
                return false;
            }
            var relativeVector = direction.clone().setComponents(direction.x - entity.x,
                    direction.y - entity.y, direction.z - entity.z);
            var xzLengthSquared = relativeVector.x * relativeVector.x + relativeVector.z * relativeVector.z;
            if (Math.abs(xzLengthSquared) < EntityPhysical.PRECISION) {
                entity.setDataFlag(ActorFlags.MOVING, false);
                return false;
            }
            var xzLength = Math.sqrt(relativeVector.x * relativeVector.x + relativeVector.z * relativeVector.z);
            var k = speed / xzLength;
            var dx = relativeVector.x * k;
            var dz = relativeVector.z * k;
            var dy = 0.0d;
            if (entity.isOnGround()) {
                double diffY = entity.getScale();
                boolean steppingUpBlock = false;
                if (collidesBlocks(entity, dx, 0, dz)) {
                    Block[] collisionBlocks = entity.level.getTickCachedCollisionBlocks(
                            entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, 0, dz),
                            false,
                            false,
                            this::canJump
                    );

                    double maxY = Arrays.stream(collisionBlocks)
                            .map(b -> b.getCollisionBoundingBox().getMaxY())
                            .max(Double::compareTo)
                            .orElse(entity.getY());

                    diffY = Math.max(diffY, maxY - entity.getY());
                    steppingUpBlock = diffY > 0.01d && diffY <= 1.1d;
                }
                dy += steppingUpBlock ? Math.max(entity.getJumpingMotion(diffY) + 0.1d, diffY + 0.05d) : entity.getJumpingMotion(diffY) + 0.1d;
                Sound jumpSound = entity instanceof EntityRabbit ? Sound.MOB_RABBIT_HOP : entity instanceof EntitySlime ? Sound.JUMP_SLIME : null;
                if(jumpSound != null) entity.getLevel().addSound(entity, jumpSound);
                entity.setDataProperty(ActorDataTypes.CLIENT_EVENT, (byte) 2);
                currentJumpCoolDown = 0;
            }
            entity.addTmpMoveMotion(new Vector3(dx, dy, dz));
            entity.setDataFlag(ActorFlags.MOVING, true);
            if (xzLength < speed) {
                needNewDirection(entity);
                return false;
            }
            return true;
        } else {
            entity.setDataFlag(ActorFlags.MOVING, false);
            return false;
        }
    }


}
