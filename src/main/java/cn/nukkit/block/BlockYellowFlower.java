package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockYellowFlower extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_flower");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowFlower() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowFlower(BlockState blockstate) {
        super(blockstate);
    }
}