package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;

public class ConditionSpawnUnderground extends Condition {

    public ConditionSpawnUnderground() {
        super("minecraft:spawns_underground");
    }

    @Override
    public boolean evaluate(Block block) {
        return block.getLevel().getHeightMap(block.getFloorX(), block.getFloorZ())-1 != block.getFloorY() && block.canPassThrough();
    }
}
