package org.powernukkitx.level.vibration;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.math.Vector3;

/**
 * @param initiator               The object which caused the vibration
 * @param source                  The vibration source position
 * @param type                    The GameEvent
 * @param sourceUniqueId          Persistent source ActorUniqueID, or 0
 * @param projectileOwnerUniqueId Persistent projectile owner ActorUniqueID, or 0
 */
public record VibrationEvent(Object initiator, Vector3 source, VibrationType type, long sourceUniqueId, long projectileOwnerUniqueId) {
    public VibrationEvent(Object initiator, Vector3 source, VibrationType type) {
        this(initiator, source, type, getSourceUniqueId(initiator), getProjectileOwnerUniqueId(initiator));
    }

    private static long getSourceUniqueId(Object initiator) {
        return initiator instanceof Entity entity ? entity.uniqueIdLong() : 0L;
    }

    private static long getProjectileOwnerUniqueId(Object initiator) {
        if (initiator instanceof EntityProjectile projectile && projectile.shootingEntity != null) {
            return projectile.shootingEntity.uniqueIdLong();
        }
        return 0L;
    }
}
