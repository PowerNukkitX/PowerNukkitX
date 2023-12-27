package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_blackstone_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneBricks(BlockState blockstate) {
        super(blockstate);
    }
}