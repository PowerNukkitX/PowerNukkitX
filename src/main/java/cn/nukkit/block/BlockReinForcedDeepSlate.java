package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockReinForcedDeepSlate extends BlockSolid{
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
