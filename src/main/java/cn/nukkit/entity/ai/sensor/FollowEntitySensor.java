package cn.nukkit.entity.ai.sensor;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * Sensor that monitors the distance to a target entity and updates a movement
 * target memory to follow it.
 *
 * When the entity moves beyond the configured start distance, the sensor
 * writes the target entity position to the follow target memory. When the
 * entity comes within the stop distance, the follow target is cleared.
 * 
 * @author Curse
 */
public class FollowEntitySensor implements ISensor {
    protected final MemoryType<Entity> entityMemory;
    protected final MemoryType<Vector3> targetMemory;
    protected final float startFollowSquared;
    protected final float stopFollowSquared;

    public FollowEntitySensor(float startFollowDistance, float stopFollowDistance) {
        this(CoreMemoryTypes.PARENT, CoreMemoryTypes.STAY_NEARBY, startFollowDistance, stopFollowDistance);
    }

    public FollowEntitySensor(MemoryType<Entity> entityMemory, MemoryType<Vector3> targetMemory, float startFollowDistance, float stopFollowDistance) {
        this.entityMemory = entityMemory;
        this.targetMemory = targetMemory;

        float start = Math.max(0f, startFollowDistance);
        float stop  = Math.max(0f, stopFollowDistance);
        if (start < stop) start = stop;

        this.startFollowSquared = start * start;
        this.stopFollowSquared  = stop * stop;
    }

    @Override
    public void sense(@NotNull EntityIntelligent entity) {
        Entity targetEntity = entity.getMemoryStorage().get(entityMemory);

        if (targetEntity == null || targetEntity.closed || !targetEntity.isAlive() || targetEntity.level != entity.level) {
            entity.getMemoryStorage().clear(targetMemory);
            return;
        }

        double d2 = entity.distanceSquared(targetEntity);

        boolean following = entity.getMemoryStorage().notEmpty(targetMemory);

        if (d2 <= stopFollowSquared) {
            entity.getMemoryStorage().clear(targetMemory);
            return;
        }

        if (following || d2 >= startFollowSquared) {
            entity.getMemoryStorage().put(
                targetMemory,
                new Vector3(targetEntity.x, targetEntity.y, targetEntity.z)
            );
        } else {
            entity.getMemoryStorage().clear(targetMemory);
        }
    }
}
