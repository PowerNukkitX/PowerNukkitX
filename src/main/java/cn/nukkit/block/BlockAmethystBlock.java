package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockAmethystBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:amethyst_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAmethystBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAmethystBlock(BlockState blockstate) {
        super(blockstate);
    }
}