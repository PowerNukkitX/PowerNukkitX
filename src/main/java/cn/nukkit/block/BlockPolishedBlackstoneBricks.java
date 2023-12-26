package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
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