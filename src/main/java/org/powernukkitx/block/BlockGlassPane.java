package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/6
 */
public class BlockGlassPane extends BlockThin {

    public static final BlockProperties PROPERTIES = new BlockProperties(GLASS_PANE);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0.3)
            .resistance(1.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGlassPane() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockGlassPane(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockGlassPane(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Glass Pane";
    }

    
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    
    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    }
