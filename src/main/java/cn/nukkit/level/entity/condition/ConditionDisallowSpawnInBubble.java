package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;

public class ConditionDisallowSpawnInBubble extends Condition{

    public ConditionDisallowSpawnInBubble() {
        super("minecraft:disallow_spawns_in_bubble");
    }

    @Override
    public boolean evaluate(Block block) {
        return !block.getLevel().getBlock(block, 1).getId().equals(Block.BUBBLE_COLUMN);
    }
}
