package cn.nukkit.block;

public abstract class BlockFroglight extends BlockSolid {

    protected BlockFroglight(BlockState blockState) {
        super(blockState);
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public double getResistance() {
        return 0.3;
    }
}
