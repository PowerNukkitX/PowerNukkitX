package org.powernukkitx.block;

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
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}
