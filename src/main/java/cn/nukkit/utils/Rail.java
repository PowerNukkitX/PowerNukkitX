package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.math.BlockFace;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static cn.nukkit.math.BlockFace.EAST;
import static cn.nukkit.math.BlockFace.NORTH;
import static cn.nukkit.math.BlockFace.SOUTH;
import static cn.nukkit.math.BlockFace.WEST;
import static cn.nukkit.utils.Rail.Orientation.State.ASCENDING;
import static cn.nukkit.utils.Rail.Orientation.State.CURVED;
import static cn.nukkit.utils.Rail.Orientation.State.STRAIGHT;

/**
 * INTERNAL helper class of railway
 * <p>
 * By lmlstarqaq http://snake1999.com/
 * Creation time: 2017/7/1 17:42.
 */

public final class Rail {
    public static boolean isRailBlock(Block block) {
        Objects.requireNonNull(block, "Rail block predicate can not accept null block");
        return isRailBlock(block.getId());
    }

    public enum Orientation {
        STRAIGHT_NORTH_SOUTH(0, STRAIGHT, NORTH, SOUTH, null),
        STRAIGHT_EAST_WEST(1, STRAIGHT, EAST, WEST, null),
        ASCENDING_EAST(2, ASCENDING, EAST, WEST, EAST),
        ASCENDING_WEST(3, ASCENDING, EAST, WEST, WEST),
        ASCENDING_NORTH(4, ASCENDING, NORTH, SOUTH, NORTH),
        ASCENDING_SOUTH(5, ASCENDING, NORTH, SOUTH, SOUTH),
        CURVED_SOUTH_EAST(6, CURVED, SOUTH, EAST, null),
        CURVED_SOUTH_WEST(7, CURVED, SOUTH, WEST, null),
        CURVED_NORTH_WEST(8, CURVED, NORTH, WEST, null),
        CURVED_NORTH_EAST(9, CURVED, NORTH, EAST, null);

        private static final Orientation[] META_LOOKUP = new Orientation[values().length];
        private final int meta;
        private final State state;
        private final List<BlockFace> connectingDirections;
        private final BlockFace ascendingDirection;

        Orientation(int meta, State state, BlockFace from, BlockFace to, BlockFace ascendingDirection) {
            this.meta = meta;
            this.state = state;
            this.connectingDirections = Arrays.asList(from, to);
            this.ascendingDirection = ascendingDirection;
        }

        public static Orientation byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public static Orientation straight(BlockFace face) {
            return switch (face) {
                case NORTH, SOUTH -> STRAIGHT_NORTH_SOUTH;
                case EAST, WEST -> STRAIGHT_EAST_WEST;
                default -> STRAIGHT_NORTH_SOUTH;
            };
        }

        public static Orientation ascending(BlockFace face) {
            return switch (face) {
                case NORTH -> ASCENDING_NORTH;
                case SOUTH -> ASCENDING_SOUTH;
                case EAST -> ASCENDING_EAST;
                case WEST -> ASCENDING_WEST;
                default -> ASCENDING_EAST;
            };
        }

        public static Orientation curved(BlockFace f1, BlockFace f2) {
            for (Orientation o : new Orientation[]{CURVED_SOUTH_EAST, CURVED_SOUTH_WEST, CURVED_NORTH_WEST, CURVED_NORTH_EAST}) {
                if (o.connectingDirections.contains(f1) && o.connectingDirections.contains(f2)) {
                    return o;
                }
            }
            return CURVED_SOUTH_EAST;
        }

        public static Orientation straightOrCurved(BlockFace f1, BlockFace f2) {
            for (Orientation o : new Orientation[]{STRAIGHT_NORTH_SOUTH, STRAIGHT_EAST_WEST, CURVED_SOUTH_EAST, CURVED_SOUTH_WEST, CURVED_NORTH_WEST, CURVED_NORTH_EAST}) {
                if (o.connectingDirections.contains(f1) && o.connectingDirections.contains(f2)) {
                    return o;
                }
            }
            return STRAIGHT_NORTH_SOUTH;
        }

        public int metadata() {
            return meta;
        }

        public boolean hasConnectingDirections(BlockFace... faces) {
            return Stream.of(faces).allMatch(connectingDirections::contains);
        }

        public boolean hasConnectingDirections(Collection<BlockFace> faces) {
            return Stream.of(faces).allMatch(connectingDirections::contains);
        }

        public List<BlockFace> connectingDirections() {
            return connectingDirections;
        }

        public Optional<BlockFace> ascendingDirection() {
            return Optional.ofNullable(ascendingDirection);
        }

        public enum State {
            STRAIGHT, ASCENDING, CURVED
        }

        public boolean isStraight() {
            return state == STRAIGHT;
        }

        public boolean isAscending() {
            return state == ASCENDING;
        }

        public boolean isCurved() {
            return state == CURVED;
        }

        static {
            for (Orientation o : values()) {
                META_LOOKUP[o.meta] = o;
            }
        }
    }

    public static boolean isRailBlock(String blockId) {
        return switch (blockId) {
            case Block.RAIL, Block.GOLDEN_RAIL, Block.ACTIVATOR_RAIL, Block.DETECTOR_RAIL -> true;
            default -> false;
        };
    }

    private Rail() {
        //no instance
    }
}
