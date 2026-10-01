package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class BlockSand extends BlockFallable implements Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(SAND);
    public static final BlockDefinition DEFINITION = FALLABLE.toBuilder()
            .hardness(0.5)
            .resistance(0.5)
            .toolType(ItemTool.TYPE_SHOVEL)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSand() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSand(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockSand(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public String getName() {
        if (this instanceof BlockRedSand) {
            return "Red Sand";
        } else {
            return "Sand";
        }
    }

}
