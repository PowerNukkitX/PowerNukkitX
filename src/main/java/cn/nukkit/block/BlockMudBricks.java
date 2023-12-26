package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMudBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mud_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMudBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMudBricks(BlockState blockstate) {
        super(blockstate);
    }
}