package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockBambooSlab extends BlockWoodenSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooSlab(BlockState blockState) {
        super(blockState, BAMBOO_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Bamboo";
    }
}