package org.powernukkitx.block;

import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.LevelException;

import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_EAST;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_NORTH;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_SOUTH;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_WEST;

/**
 * Keeps the four {@code minecraft:connection_*} sides of fences, panes and bars in step with the
 * surrounding blocks.
 *
 * @author xRookieFight
 * @since 12/09/2026
 */
final class HorizontalConnections {

    private HorizontalConnections() {
    }

    /**
     * Rewrites the block's connection sides from its surroundings.
     *
     * @return whether any side changed, so the caller only writes the block back when it has to
     */
    static <T extends Block & BlockConnectable> boolean configure(T block) {
        final short previous = block.getBlockState().specialValue();
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            set(block, face, connects(block, face));
        }
        return block.getBlockState().specialValue() != previous;
    }

    private static <T extends Block & BlockConnectable> boolean connects(T block, BlockFace face) {
        try {
            return block.canConnect(block.getSideAtLayer(0, face));
        } catch (LevelException e) {
            // the neighbor is outside a loaded chunk so leave that side alone rather than guessing
            return isSet(block, face);
        }
    }

    private static boolean isSet(Block block, BlockFace face) {
        return switch (face) {
            case NORTH -> block.getPropertyValue(CONNECTION_NORTH);
            case SOUTH -> block.getPropertyValue(CONNECTION_SOUTH);
            case WEST -> block.getPropertyValue(CONNECTION_WEST);
            case EAST -> block.getPropertyValue(CONNECTION_EAST);
            default -> false;
        };
    }

    private static void set(Block block, BlockFace face, boolean connected) {
        switch (face) {
            case NORTH -> block.setPropertyValue(CONNECTION_NORTH, connected);
            case SOUTH -> block.setPropertyValue(CONNECTION_SOUTH, connected);
            case WEST -> block.setPropertyValue(CONNECTION_WEST, connected);
            case EAST -> block.setPropertyValue(CONNECTION_EAST, connected);
            default -> {
            }
        }
    }
}
