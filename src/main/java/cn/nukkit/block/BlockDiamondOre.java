package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDiamondOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:diamond_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDiamondOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDiamondOre(BlockState blockstate) {
        super(blockstate);
    }
}