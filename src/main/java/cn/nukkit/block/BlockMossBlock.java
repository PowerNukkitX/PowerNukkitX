package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMossBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:moss_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMossBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMossBlock(BlockState blockstate) {
        super(blockstate);
    }
}