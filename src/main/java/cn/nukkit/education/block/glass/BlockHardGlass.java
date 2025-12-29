package cn.nukkit.education.block.glass;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHardGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(HARD_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHardGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHardGlass(BlockState blockstate) {
        super(blockstate);
    }
}