package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockSculkVein extends BlockLichen{
    @Override
    public String getName() {
        return "Sculk Vein";
    }

    @Override
    public int getId() {
        return BlockID.SCULK_VEIN;
    }
}
