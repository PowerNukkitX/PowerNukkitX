package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockClientRequestPlaceholderBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(CLIENT_REQUEST_PLACEHOLDER_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockClientRequestPlaceholderBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockClientRequestPlaceholderBlock(BlockState blockstate) {
        super(blockstate);
    }
}