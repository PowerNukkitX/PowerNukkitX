package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement68 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_68");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement68() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement68(BlockState blockstate) {
        super(blockstate);
    }
}