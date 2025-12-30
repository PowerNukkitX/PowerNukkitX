package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;

public class ConditionHeightFilter extends Condition {

    public final int minY, maxY;

    public ConditionHeightFilter(int minY, int  maxY) {
        super("minecraft:height_filter");
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public boolean evaluate(Block block) {
        return block.getFloorY() >= minY && block.getFloorY() <= maxY;
    }
}
