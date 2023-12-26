package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaCarpet(BlockState blockstate) {
        super(blockstate);
    }
}