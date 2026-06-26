package cn.nukkit.block;

public abstract class BlockStem extends BlockLog {

    public BlockStem(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public int getBurnChance() {
        return -1;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}
