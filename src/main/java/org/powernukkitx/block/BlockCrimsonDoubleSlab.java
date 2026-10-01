package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockDoubleSlabBase.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(3)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(-1)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonDoubleSlab(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Crimson";
    }

    @Override
    protected boolean isCorrectTool(Item item) {
        return true;
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockCrimsonSlab.PROPERTIES.getDefaultState();
    }
}