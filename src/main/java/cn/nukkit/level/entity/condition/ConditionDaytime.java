package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;

/**
 * Spawn-rule condition that is satisfied only while the level is in daytime.
 */
public class ConditionDaytime extends Condition {

    public ConditionDaytime() {
        super("pnx:daytime");
    }

    @Override
    public boolean evaluate(Block block) {
        return block.getLevel().isDaytime();
    }
}
