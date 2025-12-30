package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;
import cn.nukkit.utils.Utils;

public class ConditionProbability extends Condition {

    public final int factor, bounds;

    public ConditionProbability(int factor, int  bounds) {
        super("pnx:probability");
        this.factor = factor;
        this.bounds = bounds;
    }

    @Override
    public boolean evaluate(Block block) {
        return Utils.rand(0, bounds-1) < factor;
    }
}
