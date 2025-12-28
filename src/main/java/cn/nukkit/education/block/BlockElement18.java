package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement18 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_18");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement18() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement18(BlockState blockstate) {
        super(blockstate);
    }
}