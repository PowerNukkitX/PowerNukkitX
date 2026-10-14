package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCherryDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockDoubleSlabBase.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(15)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(5)
            .burnAbility(20)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryDoubleSlab(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Cherry";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockCherrySlab.PROPERTIES.getDefaultState();
    }
}