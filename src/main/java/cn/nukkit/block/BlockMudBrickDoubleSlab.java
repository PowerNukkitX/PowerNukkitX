package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMudBrickDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mud_brick_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMudBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMudBrickDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}