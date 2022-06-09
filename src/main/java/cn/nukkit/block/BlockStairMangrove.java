package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairMangrove extends BlockStairsWood{
    public BlockStairMangrove() {
        this(0);
    }

    public BlockStairMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_STAIRS;
    }

    @Override
    public String getName() {
        return "Mangrove Wood Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWNISH_RED;
    }
}
