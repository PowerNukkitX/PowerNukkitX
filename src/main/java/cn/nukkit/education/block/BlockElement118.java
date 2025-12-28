package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement118 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_118");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement118() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement118(BlockState blockstate) {
        super(blockstate);
    }
}