package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockInfoUpdate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(INFO_UPDATE);

    public BlockInfoUpdate() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockInfoUpdate(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Info Update Block";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

}
