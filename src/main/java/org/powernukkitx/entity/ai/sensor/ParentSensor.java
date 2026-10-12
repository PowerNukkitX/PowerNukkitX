package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityCreature;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityQueryOptions;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches for a nearby adult parent for baby entities. The sensor filters compatible living entities and stores the
 * closest valid parent in behavior memory.
 *
 * @author Curse
 */
public class ParentSensor implements ISensor {
    protected final double range;
    protected final double rangeSquared;
    protected final int period;

    /**
     * Creates a new ParentSensor instance.
     *
     * @param range value for this API
     * @param period value for this API
     */
    public ParentSensor(double range, int period) {
        this.range = Math.max(0d, range);
        this.rangeSquared = this.range * this.range;
        this.period = Math.max(1, period);
    }

    @Override
    public void sense(@NotNull EntityIntelligent entity) {
        if (!entity.isBaby()) {
            entity.getMemoryStorage().clear(CoreMemoryTypes.PARENT);
            return;
        }

        Entity current = entity.getMemoryStorage().get(CoreMemoryTypes.PARENT);
        if (isValidParent(entity, current)) return;

        entity.getMemoryStorage().clear(CoreMemoryTypes.PARENT);

        List<Entity> nearby = new ArrayList<>();
        EntityQueryOptions options = new EntityQueryOptions()
                .location(entity)
                .maxDistance(range);

        entity.getLevel().getEntities(options, nearby);

        Entity nearest = null;
        double nearestDistanceSquared = rangeSquared;

        for (Entity candidate : nearby) {
            if (!isValidParent(entity, candidate)) continue;

            double distanceSquared = entity.distanceSquared(candidate);
            if (distanceSquared >= nearestDistanceSquared) continue;

            nearest = candidate;
            nearestDistanceSquared = distanceSquared;
        }

        if (nearest != null) {
            entity.getMemoryStorage().put(CoreMemoryTypes.PARENT, nearest);
        }
    }

    private boolean isValidParent(EntityIntelligent entity, Entity candidate) {
        if (!(candidate instanceof EntityCreature creature)) return false;
        if (candidate == entity || candidate.closed || !candidate.isAlive()) return false;
        if (candidate.level != entity.level) return false;
        if (creature.isBaby()) return false;
        return creature.getIdentifier().equals(entity.getIdentifier());
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
