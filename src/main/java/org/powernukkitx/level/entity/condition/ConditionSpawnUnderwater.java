package org.powernukkitx.level.entity.condition;

import org.powernukkitx.block.Block;

public class ConditionSpawnUnderwater extends Condition{

    public ConditionSpawnUnderwater() {
        super("minecraft:spawns_underwater");
    }

    @Override
    public boolean evaluate(Block block) {
        return block.getId().equals(Block.WATER) || block.getLevelBlockAtLayer(1).getId().equals(Block.WATER);
    }
}
