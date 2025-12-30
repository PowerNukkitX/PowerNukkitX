package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.level.entity.condition.Condition;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.Getter;

public abstract class SpawnRule {

    @Getter
    private final String entityId;
    protected final ObjectArraySet<Condition> conditions;
    @Getter
    private final int herdMin, herdMax;

    protected SpawnRule(String entityId, Condition... conditions) {
        this(entityId, 1, 1, conditions);
    }

    protected SpawnRule(String entityId, int herdMin, int herdMax, Condition... conditions) {
        this.entityId = entityId;
        this.herdMin = herdMin;
        this.herdMax = herdMax;
        this.conditions = new ObjectArraySet<>(conditions);
    }

    public boolean evaluate(Block block) {
        return conditions.stream().allMatch(condition -> condition.evaluate(block));
    }

}
