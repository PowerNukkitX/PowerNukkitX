package org.powernukkitx.level.entity.condition;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockLiquid;

public class ConditionInAir extends Condition {

    public ConditionInAir() {
        super("pnx:in_air");
    }

    @Override
    public boolean evaluate(Block block) {
        return block.canPassThrough() && !(block instanceof BlockLiquid);
    }
}
