package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
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

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWNISH_RED;
    }
}
