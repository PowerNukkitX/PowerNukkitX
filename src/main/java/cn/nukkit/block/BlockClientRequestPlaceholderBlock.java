package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockClientRequestPlaceholderBlock extends Block {
    public static final BlockProperties $1 = new BlockProperties(CLIENT_REQUEST_PLACEHOLDER_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockClientRequestPlaceholderBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockClientRequestPlaceholderBlock(BlockState blockstate) {
        super(blockstate);
    }
}