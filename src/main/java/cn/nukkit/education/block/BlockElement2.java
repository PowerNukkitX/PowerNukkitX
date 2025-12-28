package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement2 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_2");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement2() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement2(BlockState blockstate) {
        super(blockstate);
    }
}