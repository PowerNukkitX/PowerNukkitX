package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

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
