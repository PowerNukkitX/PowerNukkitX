package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;

/**
 * @author Buddelbubi
 * @since 2025/11/18
 */
public class ConditionTrue extends Condition {

    public ConditionTrue() {
        super("pnx:true");
    }

    @Override
    public boolean evaluate(Block block) {
        return true;
    }
}
