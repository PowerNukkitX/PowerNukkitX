package org.powernukkitx.level.entity.condition;

import org.powernukkitx.block.Block;

public class ConditionSpawnOnSurface extends Condition {

    public ConditionSpawnOnSurface() {
        super("minecraft:spawns_on_surface");
    }

    @Override
    public boolean evaluate(Block block) {
        return block.getLevel().getHeightMap(block.getFloorX(), block.getFloorZ()) == block.getFloorY()-1;
    }
}
