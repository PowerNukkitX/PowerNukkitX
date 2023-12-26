package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockIronBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:iron_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockIronBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockIronBlock(BlockState blockstate) {
        super(blockstate);
    }
}