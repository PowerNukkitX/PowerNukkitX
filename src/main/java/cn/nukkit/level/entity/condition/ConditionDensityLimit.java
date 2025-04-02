package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;

import java.util.Arrays;

public class ConditionDensityLimit extends Condition {

    private final String entityId;
    public final int limit;
    public final int radius;

    public ConditionDensityLimit(String entityId, int limit) {
        this(entityId, limit, 32);
    }

    public ConditionDensityLimit(String entityId, int limit, int radius) {
        super("minecraft:density_limit");
        this.entityId = entityId;
        this.limit = limit;
        this.radius = radius;
    }

    @Override
    public boolean evaluate(Block block) {
        return Arrays.stream(block.getLevel().getEntities()).filter(entity -> entity.distance(block) < radius && entity.getIdentifier().equals(entityId)).count() < limit;
    }

}
