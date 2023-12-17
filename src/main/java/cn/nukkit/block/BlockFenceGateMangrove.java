package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockFenceGateMangrove extends BlockFenceGate{
    public BlockFenceGateMangrove() {
        this(0);
    }

    public BlockFenceGateMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Mangrove Fence Gate";
    }

}
