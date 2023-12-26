package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_exposed_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCopper(BlockState blockstate) {
        super(blockstate);
    }
}