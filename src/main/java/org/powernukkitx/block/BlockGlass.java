package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockGlass extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(GLASS);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0.3)
            .resistance(0.3)
            .canSilkTouch(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGlass() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockGlass(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockGlass(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public String getName() {
        return "Glass";
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    
}
