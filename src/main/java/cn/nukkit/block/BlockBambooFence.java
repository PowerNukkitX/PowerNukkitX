package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockBambooFence extends BlockFenceBase {
    public BlockBambooFence() {
    }

    public int getId() {
        return BAMBOO_FENCE;
    }

    public String getName() {
        return "Bamboo Fence";
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