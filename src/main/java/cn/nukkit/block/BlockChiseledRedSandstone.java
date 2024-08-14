package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledRedSandstone extends BlockSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_RED_SANDSTONE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledRedSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledRedSandstone(BlockState blockstate) {
        super(blockstate);
    }
}
