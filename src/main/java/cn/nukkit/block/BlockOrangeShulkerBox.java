package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}