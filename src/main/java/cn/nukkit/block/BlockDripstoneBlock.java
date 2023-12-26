package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDripstoneBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dripstone_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDripstoneBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDripstoneBlock(BlockState blockstate) {
        super(blockstate);
    }
}