package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrackedPolishedBlackstoneBricks extends BlockBlackstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRACKED_POLISHED_BLACKSTONE_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrackedPolishedBlackstoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedPolishedBlackstoneBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cracked Polished Blackstone Bricks";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}