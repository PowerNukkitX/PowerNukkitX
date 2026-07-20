package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.IN_WALL_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.OPEN_BIT;

public class BlockMangroveFenceGate extends BlockFenceGate {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_FENCE_GATE, IN_WALL_BIT, MINECRAFT_CARDINAL_DIRECTION, OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveFenceGate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mangrove Fence Gate";
    }
}