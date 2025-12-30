package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;

public class ConditionBrightnessFilter extends Condition {

    public final int min, max;

    public ConditionBrightnessFilter(int min, int  max) {
        super("minecraft:brightness_filter");
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean evaluate(Block block) {
        int lightLevel = block.getLevel().getFullLight(block);
        return lightLevel >= min && lightLevel <= max;
    }
}
