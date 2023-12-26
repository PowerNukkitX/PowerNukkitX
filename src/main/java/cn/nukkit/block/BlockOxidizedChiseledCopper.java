package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oxidized_chiseled_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}