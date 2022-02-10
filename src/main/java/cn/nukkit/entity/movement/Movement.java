package cn.nukkit.entity.movement;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public interface Movement {
    /**
     * 移动精度阈值，绝对值小于此阈值的移动被视为没有移动
     */
    double PRECISION = 0.00001d;

    default void handleAIMovement() {

    }

    default void handleLiquidMovement() {

    }

    default void handleGravityMovement() {

    }

    default void handleFrictionMovement() {

    }

    /**
     * @return 返回实体自身
     */
    Entity getSelf();
}
