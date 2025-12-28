package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement16 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_16");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement16() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement16(BlockState blockstate) {
        super(blockstate);
    }
}