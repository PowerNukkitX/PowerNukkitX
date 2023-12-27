package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:wooden_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.WOOD_TYPE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWoodenSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWoodenSlab(BlockState blockstate) {
        super(blockstate);
    }
}