package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockChiseledTuff extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:chiseled_tuff");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledTuff() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledTuff(BlockState blockstate) {
        super(blockstate);
    }
}