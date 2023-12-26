package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleWool(BlockState blockstate) {
        super(blockstate);
    }
}