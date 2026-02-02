package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockAncientDebris extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(ANCIENT_DEBRIS);

    public static final BlockDefinition DEFINITION = BlockSolid.SOLID.toBuilder()
            .hardness(30)
            .resistance(1200)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_DIAMOND)
            .canHarvestWithHand(false)
            .lavaResistant(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAncientDebris() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAncientDebris(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Ancient Derbris";
    }
}
