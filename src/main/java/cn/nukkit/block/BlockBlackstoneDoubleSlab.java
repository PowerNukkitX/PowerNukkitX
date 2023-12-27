package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlackstoneDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blackstone_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackstoneDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackstoneDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}