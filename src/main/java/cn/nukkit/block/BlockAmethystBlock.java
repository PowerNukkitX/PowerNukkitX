package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockAmethystBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(AMETHYST_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(1.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_IRON)
            .canHarvestWithHand(false)
            .lavaResistant(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAmethystBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAmethystBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Amethyst Block";
    }
}