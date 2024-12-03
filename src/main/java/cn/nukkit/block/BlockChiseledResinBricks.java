package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledResinBricks extends BlockResinBricks {

    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_RESIN_BRICKS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledResinBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledResinBricks(BlockState blockstate) {
        super(blockstate);
    }
}
