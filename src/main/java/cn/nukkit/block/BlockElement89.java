package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement89 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_89");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement89() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement89(BlockState blockstate) {
        super(blockstate);
    }
}