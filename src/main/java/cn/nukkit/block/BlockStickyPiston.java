package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStickyPiston extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:sticky_piston", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStickyPiston() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStickyPiston(BlockState blockstate) {
        super(blockstate);
    }
}