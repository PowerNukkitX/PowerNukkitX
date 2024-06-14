package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceDoubleSlab extends BlockDoubleWoodenSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Spruce";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockSpruceSlab.PROPERTIES.getDefaultState();
    }
}