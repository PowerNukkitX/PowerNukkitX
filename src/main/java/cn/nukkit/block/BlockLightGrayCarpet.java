package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayCarpet(BlockState blockstate) {
        super(blockstate);
    }
}