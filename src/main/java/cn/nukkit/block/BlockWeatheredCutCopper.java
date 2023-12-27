package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCutCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:weathered_cut_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCutCopper(BlockState blockstate) {
        super(blockstate);
    }
}