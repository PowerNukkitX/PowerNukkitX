package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockInfoUpdate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:info_update");

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
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

}
