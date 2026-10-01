package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;


public class BlockMangroveRoots extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_ROOTS);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0.7)
            .resistance(0.7)
            .burnChance(5)
            .waterloggingLevel(1)
            .build();
    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveRoots() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMangroveRoots(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Mangrove Roots";
    }

    
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        return level.setBlock(this, this);
    }
}
