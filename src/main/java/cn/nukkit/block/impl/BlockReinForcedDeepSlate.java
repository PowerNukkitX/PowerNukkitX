package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockSolid;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockReinForcedDeepSlate extends BlockSolid {
    @Override
    public String getName() {
        return "ReinForced DeepSlate";
    }

    @Override
    public int getId() {
        return REINFORCED_DEEPSLATE;
    }

    @Override
    public double getResistance() {
        return 1200.0;
    }
}
