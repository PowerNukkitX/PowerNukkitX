package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledPolishedBlackstone extends BlockBlackstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_POLISHED_BLACKSTONE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledPolishedBlackstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledPolishedBlackstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Chiseled Polished Blackstone";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}