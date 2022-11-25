package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * 封装了一些涉及控制器方法的执行器.
 * <p>
 * Involving some methods about controller.
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
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
