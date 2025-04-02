package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;
import cn.nukkit.registry.Registries;

import java.util.Arrays;

public class ConditionNot extends Condition {

    public final Condition condition;

    public ConditionNot(Condition condition) {
        super("pnx:not");
        this.condition = condition;
    }

    @Override
    public boolean evaluate(Block block) {
        return !condition.evaluate(block);
    }
}
