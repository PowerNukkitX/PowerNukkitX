package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement33 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_33");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement33() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement33(BlockState blockstate) {
        super(blockstate);
    }
}