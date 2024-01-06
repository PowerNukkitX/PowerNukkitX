package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPackedMud extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(PACKED_MUD);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPackedMud() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPackedMud(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Packed Mud";
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 3;
    }
}
