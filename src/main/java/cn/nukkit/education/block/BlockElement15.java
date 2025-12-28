package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement15 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_15");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement15() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement15(BlockState blockstate) {
        super(blockstate);
    }
}