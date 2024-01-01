package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrackedPolishedBlackstoneBricks extends BlockBlackstone {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cracked_polished_blackstone_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
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