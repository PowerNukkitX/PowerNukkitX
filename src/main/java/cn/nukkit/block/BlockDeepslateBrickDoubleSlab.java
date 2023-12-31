package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateBrickDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_BRICK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateBrickDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}