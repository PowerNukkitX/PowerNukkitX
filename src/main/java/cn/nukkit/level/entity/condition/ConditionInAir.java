package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;

public class ConditionInAir extends Condition {

    public ConditionInAir() {
        super("pnx:in_air");
    }

    @Override
    public boolean evaluate(Block block) {
        return block.canPassThrough() && !(block instanceof BlockLiquid);
    }
}
