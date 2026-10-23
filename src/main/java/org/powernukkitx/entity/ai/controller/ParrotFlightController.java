package org.powernukkitx.entity.ai.controller;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.passive.EntityParrot;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

/** Provides flight acceleration while allowing parrots to land and sit. */
public class ParrotFlightController implements IController {
    @Override
    public boolean control(EntityIntelligent entity) {
        if (!(entity instanceof EntityParrot parrot) || parrot.isRiding()) return false;
        boolean moving = parrot.getMoveTarget() != null && parrot.hasMoveDirection()
                && !parrot.isShouldUpdateMoveDirection();
        parrot.setDataFlag(ActorFlags.MOVING, moving);
        if (!moving) return false;
        Vector3 direction = parrot.getMoveDirectionEnd();
        double dx = direction.x - parrot.x;
        double dy = direction.y - parrot.y;
        double dz = direction.z - parrot.z;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 0.2) {
            parrot.setShouldUpdateMoveDirection(true);
            if (parrot.distanceSquared(parrot.getMoveTarget()) < 0.5) parrot.setMoveTarget(null);
            return false;
        }
        double factor = parrot.getMovementSpeed() * 0.33 / length;
        parrot.addTmpMoveMotion(new Vector3(
                dx * factor - parrot.motionX * 0.33,
                dy * factor - parrot.motionY * 0.33 + parrot.getGravity(),
                dz * factor - parrot.motionZ * 0.33));
        return true;
    }
}
