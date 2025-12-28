package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement75 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_75");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement75() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement75(BlockState blockstate) {
        super(blockstate);
    }
}