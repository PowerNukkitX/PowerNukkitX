package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;

public class ConditionSpawnUnderwater extends Condition{

    public ConditionSpawnUnderwater() {
        super("minecraft:spawns_underwater");
    }

    @Override
    public boolean evaluate(Block block) {
        return block.getId().equals(Block.WATER);
    }
}
