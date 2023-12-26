package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBirchPlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:birch_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchPlanks(BlockState blockstate) {
        super(blockstate);
    }
}