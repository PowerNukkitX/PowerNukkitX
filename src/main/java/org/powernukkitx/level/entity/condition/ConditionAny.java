package org.powernukkitx.level.entity.condition;

import org.powernukkitx.block.Block;

import java.util.Arrays;

public class ConditionAny extends Condition {

    public final Condition[] conditions;

    public ConditionAny(Condition... condition) {
        super("pnx:any");
        this.conditions = condition;
    }

    @Override
    public boolean evaluate(Block block) {
        return Arrays.stream(conditions).anyMatch(condition -> condition.evaluate(block));
    }
}
