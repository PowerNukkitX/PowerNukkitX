package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDiamondBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:diamond_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDiamondBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDiamondBlock(BlockState blockstate) {
        super(blockstate);
    }
}