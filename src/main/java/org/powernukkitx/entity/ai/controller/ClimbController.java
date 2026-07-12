package org.powernukkitx.entity.ai.controller;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityPhysical;
import org.powernukkitx.math.BVector3;
import org.powernukkitx.math.Vector3;

public class ClimbController implements IController {
    @Override
    public boolean control(EntityIntelligent entity) {
        if (!entity.canClimb()) {
            entity.setWallClimbing(false);
            return false;
        }

        Vector3 target = entity.getMoveTarget();
        if (target == null && entity.targetEntity != null && entity.targetEntity.isAlive() && entity.targetEntity.level == entity.level) {
            target = entity.targetEntity.getLocation();
        }

        if (target == null) {
            entity.setWallClimbing(false);
            return false;
        }

        double dx = target.x - entity.x;
        double dz = target.z - entity.z;
        double xzLengthSquared = dx * dx + dz * dz;
        if (xzLengthSquared < EntityPhysical.PRECISION) {
            entity.setWallClimbing(false);
            return false;
        }

        double xzLength = Math.sqrt(xzLengthSquared);
        double speed = entity.isCollidedHorizontally ? entity.getMovementSpeed() * 0.33d : entity.getMovementSpeed() * 0.1d;
        double yaw = BVector3.getYawFromVector(new Vector3(dx, 0, dz));
        entity.setYaw(yaw);
        entity.setHeadYaw(yaw);
        boolean wallClimbing = entity.isCollidedHorizontally;
        entity.setWallClimbing(wallClimbing);
        double climbSpeed = Math.min(speed, 0.16d);
        double dy = wallClimbing ? Math.max(-climbSpeed, Math.min(climbSpeed, target.y - entity.y)) : 0;
        entity.addTmpMoveMotion(new Vector3(dx / xzLength * speed, dy, dz / xzLength * speed));
        entity.setDataFlag(ActorFlags.MOVING, true);
        return true;
    }
}
