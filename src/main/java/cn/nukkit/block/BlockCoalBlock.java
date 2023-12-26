package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCoalBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:coal_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoalBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoalBlock(BlockState blockstate) {
        super(blockstate);
    }
}