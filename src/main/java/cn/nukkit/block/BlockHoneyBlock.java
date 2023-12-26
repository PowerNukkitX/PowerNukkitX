package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHoneyBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:honey_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHoneyBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHoneyBlock(BlockState blockstate) {
        super(blockstate);
    }
}