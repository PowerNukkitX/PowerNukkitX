package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRawIronBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(RAW_IRON_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRawIronBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRawIronBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Block of Raw Iron";
    }
}