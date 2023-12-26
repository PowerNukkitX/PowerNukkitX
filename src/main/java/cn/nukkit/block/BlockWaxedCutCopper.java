package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedCutCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_cut_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCutCopper(BlockState blockstate) {
        super(blockstate);
    }
}