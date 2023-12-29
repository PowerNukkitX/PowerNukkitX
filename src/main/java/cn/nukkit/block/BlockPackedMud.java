package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPackedMud extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:packed_mud");

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
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
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
