package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

public class BlockRawCopperBlock extends BlockRaw {
    public static final BlockProperties PROPERTIES = new BlockProperties(RAW_COPPER_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRawCopperBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRawCopperBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Block of Raw Copper";
    }
}