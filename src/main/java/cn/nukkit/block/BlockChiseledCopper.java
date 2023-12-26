package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:chiseled_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}