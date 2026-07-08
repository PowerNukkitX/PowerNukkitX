package org.powernukkitx.level.entity.spawners;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.entity.condition.Condition;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.Getter;

public abstract class SpawnRule {

    @Getter
    private final String entityId;
    protected final ObjectArraySet<Condition> conditions;
    @Getter
    private final int herdMin, herdMax;
    @Getter
    private final int weight;

    protected SpawnRule(String entityId, Condition... conditions) {
        this(entityId, 1, 1, conditions);
    }

    protected SpawnRule(String entityId, int weight, Condition... conditions) {
        this(entityId, 1, 1, weight, conditions);
    }

    protected SpawnRule(String entityId, int herdMin, int herdMax, Condition... conditions) {
        this(entityId, herdMin, herdMax, 1, conditions);
    }

    protected SpawnRule(String entityId, int herdMin, int herdMax, int weight, Condition... conditions) {
        this.entityId = entityId;
        this.herdMin = herdMin;
        this.herdMax = herdMax;
        this.weight = Math.max(1, weight);
        this.conditions = new ObjectArraySet<>(conditions);
    }

    public boolean evaluate(Block block) {
        return conditions.stream().allMatch(condition -> condition.evaluate(block));
    }

}
