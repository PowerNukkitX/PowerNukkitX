package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMangrovePlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangrovePlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangrovePlanks(BlockState blockstate) {
        super(blockstate);
    }
}