package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockSprucePlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:spruce_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSprucePlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSprucePlanks(BlockState blockstate) {
        super(blockstate);
    }
}