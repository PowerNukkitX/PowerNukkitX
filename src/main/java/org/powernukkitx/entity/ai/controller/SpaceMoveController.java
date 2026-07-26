package org.powernukkitx.entity.ai.controller;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

/**
 * Dealing with physical movement in flight/swimming
 */
public class SpaceMoveController implements IController {
    @Override
    public boolean control(EntityIntelligent entity) {
        if (entity.hasMoveDirection() && !entity.isShouldUpdateMoveDirection()) {
            Vector3 direction = entity.getMoveDirectionEnd();
            var speed = entity.getMovementSpeed();
            if (entity.motionX * entity.motionX + entity.motionZ * entity.motionZ > speed * speed * 0.4756) {
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
            entity.setDataFlag(ActorFlags.MOVING, true);
            if (xyzLength < speed) {
                needNewDirection(entity);
                return false;
            }
            return true;
        } else {
            entity.setDataFlag(ActorFlags.MOVING, false);
            return false;
        }
    }

    protected void needNewDirection(EntityIntelligent entity) {
        entity.setShouldUpdateMoveDirection(true);
    }
}
