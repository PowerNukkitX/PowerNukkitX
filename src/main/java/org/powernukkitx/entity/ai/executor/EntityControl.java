package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * 封装了一些涉及控制器的方法.
 * <p>
 * Involving some methods about controller.
 */


public interface EntityControl {

    default void setRouteTarget(@NotNull EntityIntelligent entity, Vector3 vector3) {
        entity.setMoveTarget(vector3);
    }

    default void setLookTarget(@NotNull EntityIntelligent entity, Vector3 vector3) {
        entity.setLookTarget(vector3);
    }

    default void removeRouteTarget(@NotNull EntityIntelligent entity) {
        entity.setMoveTarget(null);
    }

    default void removeLookTarget(@NotNull EntityIntelligent entity) {
        entity.setLookTarget(null);
    }
}
