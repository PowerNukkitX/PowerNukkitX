package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}