package org.powernukkitx.level.entity.condition;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockLeaves;

public class ConditionSpawnOnGround extends Condition {

    private final boolean allowWater;

    public ConditionSpawnOnGround() {
        this(false);
    }

    public ConditionSpawnOnGround(boolean allowWater) {
        super("pnx:spawns_on_ground");
        this.allowWater = allowWater;
    }

    @Override
    public boolean evaluate(Block block) {
        return block.getLevel().standable(block, allowWater, false) && !(block.down() instanceof BlockLeaves);
    }
}
