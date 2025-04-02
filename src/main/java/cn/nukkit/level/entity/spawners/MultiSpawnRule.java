package cn.nukkit.level.entity.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.level.entity.condition.Condition;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

public abstract class MultiSpawnRule extends SpawnRule {

    private String entityId;
    private int herdMin, herdMax;

    private final ObjectArraySet<SpawnRule> spawnRules;

    protected MultiSpawnRule(Condition[] conditions, SpawnRule... spawnRules) {
        super("", conditions);
        this.spawnRules = new ObjectArraySet<>(spawnRules);
    }

    @Override
    public boolean evaluate(Block block) {
        for(Condition condition : conditions) {
            if(!condition.evaluate(block)) return false;
        }
        for(SpawnRule rule : spawnRules) {
            if(rule.evaluate(block)) {
                this.entityId = rule.getEntityId();
                this.herdMin = rule.getHerdMin();
                this.herdMax = rule.getHerdMax();
                return true;
            }
        }
        return false;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public int getHerdMin() {
        return herdMin;
    }

    @Override
    public int getHerdMax() {
        return herdMax;
    }
}
