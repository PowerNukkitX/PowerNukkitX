package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockBambooMosaicSlab extends BlockWoodenSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_MOSAIC_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooMosaicSlab(BlockState blockState) {
        super(blockState, BAMBOO_MOSAIC_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Bamboo Mosaic";
    }
}