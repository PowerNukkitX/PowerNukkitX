package cn.nukkit.entity.movement;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public interface DefaultFrictionMovement extends Movement {
    @Override
    default void handleFrictionMovement() {
        final Entity entity = getSelf();
        // 减少移动向量（计算摩擦系数，在冰上滑得更远）
        final double friction = entity.getLevel().getBlock(entity.temporalVector.setComponents((int) Math.floor(entity.x), (int) Math.floor(entity.y - 1), (int) Math.floor(entity.z) - 1)).getFrictionFactor();
        final double reduce = getMovementSpeed() * (1 - friction * 0.7) * 0.43;
        if (Math.abs(entity.motionZ) < PRECISION && Math.abs(entity.motionX) < PRECISION) {
            return;
        }
        final double angle = StrictMath.atan2(entity.motionZ, entity.motionX);
        double tmp = (StrictMath.cos(angle) * reduce);
        if (entity.motionX > PRECISION) {
            entity.motionX -= tmp;
            if (entity.motionX < PRECISION) {
                entity.motionX = 0;
            }
        } else if (entity.motionX < -PRECISION) {
            entity.motionX -= tmp;
            if (entity.motionX > -PRECISION) {
                entity.motionX = 0;
            }
        } else {
            entity.motionX = 0;
        }
        tmp = (StrictMath.sin(angle) * reduce);
        if (entity.motionZ > PRECISION) {
            entity.motionZ -= tmp;
            if (entity.motionZ < PRECISION) {
                entity.motionZ = 0;
            }
        } else if (entity.motionZ < -PRECISION) {
            entity.motionZ -= tmp;
            if (entity.motionZ > -PRECISION) {
                entity.motionZ = 0;
            }
        } else {
            entity.motionZ = 0;
        }
    }

    /**
     * @return 该实体的移动速度
     */
    float getMovementSpeed();
}
