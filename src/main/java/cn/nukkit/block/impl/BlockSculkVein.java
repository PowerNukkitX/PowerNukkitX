package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLichen;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockSculkVein extends BlockLichen {
    @Override
    public String getName() {
        return "Sculk Vein";
    }

    @Override
    public int getId() {
        return BlockID.SCULK_VEIN;
    }
}
