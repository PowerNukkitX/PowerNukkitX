package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockAndesite extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(ANDESITE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAndesite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAndesite(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }
}