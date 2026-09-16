package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.IN_WALL_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.OPEN_BIT;

public class BlockPoplarFenceGate extends BlockFenceGate {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_FENCE_GATE, IN_WALL_BIT, MINECRAFT_CARDINAL_DIRECTION, OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoplarFenceGate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Poplar Fence Gate";
    }
}
