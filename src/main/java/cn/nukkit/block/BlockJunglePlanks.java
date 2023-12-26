package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockJunglePlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:jungle_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJunglePlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJunglePlanks(BlockState blockstate) {
        super(blockstate);
    }
}