package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockBambooFenceGate extends BlockFenceGate {
    public BlockBambooFenceGate() {
    }

    public int getId() {
        return BAMBOO_FENCE_GATE;
    }

    public String getName() {
        return "Bamboo Fence Gate";
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }
}