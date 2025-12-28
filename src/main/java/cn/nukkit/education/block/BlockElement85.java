package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement85 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_85");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement85() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement85(BlockState blockstate) {
        super(blockstate);
    }
}