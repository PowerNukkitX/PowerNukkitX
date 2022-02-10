package cn.nukkit.entity.movement;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public interface DefaultGravityMovement extends Movement {
    @Override
    default void handleGravityMovement() {
        final Entity entity = getSelf();
        if (!entity.onGround) {
            final int fallingTick = getFallingTick();
            if (fallingTick == 0) {
                entity.motionY -= entity.getGravity();
            } else {
                entity.motionY += (StrictMath.pow(entity.getGravity() * 12.25, fallingTick * 0.5) - 1) * 3.92;
            }
        }
    }

    /**
     * @return 该实体在空中掉落的时长
     */
    int getFallingTick();
}
