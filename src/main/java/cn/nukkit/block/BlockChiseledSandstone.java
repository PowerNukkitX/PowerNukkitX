package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledSandstone extends BlockSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_SANDSTONE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledSandstone(BlockState blockstate) {
        super(blockstate);
    }
}
