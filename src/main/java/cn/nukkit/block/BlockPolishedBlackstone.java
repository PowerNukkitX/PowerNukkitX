package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstone extends BlockBlackstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Polished Blackstone";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }
}