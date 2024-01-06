package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

public class BlockBambooMosaic extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_MOSAIC);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooMosaic() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooMosaic(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Bamboo Mosaic";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }
}