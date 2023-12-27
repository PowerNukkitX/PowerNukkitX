package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockClientRequestPlaceholderBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:client_request_placeholder_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockClientRequestPlaceholderBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockClientRequestPlaceholderBlock(BlockState blockstate) {
        super(blockstate);
    }
}