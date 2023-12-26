package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_oxidized_chiseled_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}