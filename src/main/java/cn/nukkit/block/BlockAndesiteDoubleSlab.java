package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockAndesiteDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(ANDESITE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public static final BlockDefinition DEFINITION = BlockDoubleSlabBase.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAndesiteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAndesiteDoubleSlab(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Andesite";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockAndesiteSlab.PROPERTIES.getDefaultState();
    }
}