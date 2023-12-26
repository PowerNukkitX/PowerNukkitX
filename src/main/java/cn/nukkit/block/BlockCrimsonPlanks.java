package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonPlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonPlanks(BlockState blockstate) {
        super(blockstate);
    }
}