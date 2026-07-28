package org.powernukkitx.level.entity.condition;

import org.powernukkitx.block.Block;

public class ConditionBiomeIdFilter extends Condition {

    public final int[] biomeIds;

    public ConditionBiomeIdFilter(int... biomeIds) {
        super("minecraft:biome_id_filter");
        this.biomeIds = biomeIds;
    }

    @Override
    public boolean evaluate(Block block) {
        int biomeId = block.getLevel().getBiomeId(block.getFloorX(), block.getFloorY(), block.getFloorZ());
        for (int id : biomeIds) {
            if (id == biomeId) return true;
        }
        return false;
    }
}
