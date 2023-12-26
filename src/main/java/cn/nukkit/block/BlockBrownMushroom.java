package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBrownMushroom extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_mushroom");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownMushroom() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownMushroom(BlockState blockstate) {
        super(blockstate);
    }
}