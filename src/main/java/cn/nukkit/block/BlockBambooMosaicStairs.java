package cn.nukkit.block;


public class BlockBambooMosaicStairs extends BlockStairs {
    public BlockBambooMosaicStairs() {
        this(0);
    }

    public BlockBambooMosaicStairs(int meta) {
        super(meta);
    }

    public int getId() {
        return BAMBOO_MOSAIC_STAIRS;
    }

    public String getName() {
        return "Bamboo Mosaic Stairs";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }
}