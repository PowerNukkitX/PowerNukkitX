package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockRedstoneBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:redstone_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedstoneBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedstoneBlock(BlockState blockstate) {
        super(blockstate);
    }
}