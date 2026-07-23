package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockStoneButton extends BlockButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockButton.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneButton(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockStoneButton(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Stone Button";
    }

    @Override
    public Item[] getDrops(Item item) {
        return super.getDrops(item);
    }
}