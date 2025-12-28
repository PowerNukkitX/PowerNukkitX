package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement78 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_78");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement78() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement78(BlockState blockstate) {
        super(blockstate);
    }
}