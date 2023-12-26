package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBrickSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:tuff_brick_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffBrickSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBrickSlab(BlockState blockstate) {
        super(blockstate);
    }
}