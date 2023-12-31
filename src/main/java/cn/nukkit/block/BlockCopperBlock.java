package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCopperBlock extends BlockCopperBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_BLOCK);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Block of Copper";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}