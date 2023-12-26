package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCutCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oxidized_cut_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCutCopper(BlockState blockstate) {
        super(blockstate);
    }
}