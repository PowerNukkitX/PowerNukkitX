package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedCutCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_oxidized_cut_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedCutCopper(BlockState blockstate) {
        super(blockstate);
    }
}