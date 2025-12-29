package cn.nukkit.education.block.elements;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement62 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_62");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement62() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement62(BlockState blockstate) {
        super(blockstate);
    }
}