package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBrickDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:tuff_brick_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBrickDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}