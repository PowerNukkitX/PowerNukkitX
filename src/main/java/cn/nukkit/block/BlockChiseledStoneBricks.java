package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledStoneBricks extends BlockStoneBricks {

    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_STONE_BRICKS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledStoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledStoneBricks(BlockState blockstate) {
        super(blockstate);
    }

}
