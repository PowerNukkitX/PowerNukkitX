package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;

public class ConditionSpawnUnderground extends Condition {

    public ConditionSpawnUnderground() {
        super("minecraft:spawns_underground");
    }

    @Override
    public boolean evaluate(Block block) {
        int highest = block.getLevel().getHeightMap(block.getFloorX(), block.getFloorZ());
        return highest > block.getFloorY() && block.canPassThrough() &&
                block.getLevel().getBlock(block.getFloorX(), highest, block.getFloorZ()).isSolid();
    }
}
