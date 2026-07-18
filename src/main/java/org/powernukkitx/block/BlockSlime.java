package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author Pub4Game
 * @since 21.02.2016
 */
public class BlockSlime extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(SLIME);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0)
            .resistance(0)
            .canStickBlocks(true)
            .isSolid(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSlime() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSlime(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    
    @Override
    public String getName() {
        return "Slime Block";
    }

    
    @Override
    public int getLightFilter() {
        return 1;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return true;
    }
}
