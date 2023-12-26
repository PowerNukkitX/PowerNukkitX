package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement25 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_25");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement25() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement25(BlockState blockstate) {
        super(blockstate);
    }
}