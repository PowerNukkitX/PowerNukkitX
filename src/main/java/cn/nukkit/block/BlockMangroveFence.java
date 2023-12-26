package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveFence(BlockState blockstate) {
        super(blockstate);
    }
}