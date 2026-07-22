package org.powernukkitx.utils;

import org.cloudburstmc.protocol.bedrock.data.payload.structure.Rotation;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockJigsaw;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockTrapdoor;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.block.property.enums.LeverDirection;
import org.powernukkitx.block.property.enums.MinecraftCardinalDirection;
import org.powernukkitx.block.property.enums.WallConnectionType;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.block.property.type.EnumPropertyType;
import org.powernukkitx.math.BlockFace;
import lombok.experimental.UtilityClass;

import java.util.EnumMap;

import static org.powernukkitx.block.property.CommonBlockProperties.*;
import static org.powernukkitx.block.property.CommonBlockProperties.HUGE_MUSHROOM_BITS;
import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.PILLAR_AXIS;
import static org.powernukkitx.block.property.CommonBlockProperties.VINE_DIRECTION_BITS;
import static org.powernukkitx.block.property.CommonBlockProperties.WALL_CONNECTION_TYPE_EAST;
import static org.powernukkitx.block.property.CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH;
import static org.powernukkitx.block.property.CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH;
import static org.powernukkitx.block.property.CommonBlockProperties.WALL_CONNECTION_TYPE_WEST;
import static org.powernukkitx.block.property.enums.TorchFacingDirection.EAST;
import static org.powernukkitx.block.property.enums.TorchFacingDirection.NORTH;
import static org.powernukkitx.block.property.enums.TorchFacingDirection.SOUTH;
import static org.powernukkitx.block.property.enums.TorchFacingDirection.UNKNOWN;
import static org.powernukkitx.block.property.enums.TorchFacingDirection.WEST;

/**
 * @author Kaooot
 */
@UtilityClass
public class StructureRotationUtil {

    public static BlockState clockwise90(BlockState state) {
        Block block = state.toBlock();
        var states = new java.util.ArrayList<>(state.getBlockPropertyValues());
        EnumMap<BlockFace, WallConnectionType> rotatedWallConnections = null;
        if(state.getPropertyValue(WALL_POST_BIT) != null) {
            rotatedWallConnections = rotateWallConnectionsClockwise90(state);
        }

        int idx = 0;
        for (var property : state.getBlockPropertyValues()) {
            var type = property.getPropertyType();

            if (type == TORCH_FACING_DIRECTION) {
                var rotated = TORCH_FACING_DIRECTION.createValue(switch (state.getPropertyValue(TORCH_FACING_DIRECTION)) {
                    case NORTH -> EAST;
                    case EAST -> SOUTH;
                    case SOUTH -> WEST;
                    case WEST -> NORTH;
                    default -> UNKNOWN;
                });
                states.set(idx, rotated);
            } else if (type == RAIL_DIRECTION_10) {
                var rotated = RAIL_DIRECTION_10.createValue(switch (state.getPropertyValue(RAIL_DIRECTION_10)) {
                    case 0 -> 1;
                    case 2 -> 5;
                    case 3 -> 4;
                    case 4 -> 2;
                    case 5 -> 3;
                    case 6 -> 7;
                    case 7 -> 8;
                    case 8 -> 9;
                    case 9 -> 6;
                    default -> 0;
                });
                states.set(idx, rotated);
            } else if (type == RAIL_DIRECTION_6) {
                var rotated = RAIL_DIRECTION_6.createValue(switch (state.getPropertyValue(RAIL_DIRECTION_6)) {
                    case 0 -> 1;
                    case 2 -> 5;
                    case 3 -> 4;
                    case 4 -> 2;
                    case 5 -> 3;
                    default -> 0;
                });
                states.set(idx, rotated);
            } else if (type == WEIRDO_DIRECTION) {
                var rotated = WEIRDO_DIRECTION.createValue(switch (state.getPropertyValue(WEIRDO_DIRECTION)) {
                    case 0 -> 2;
                    case 1 -> 3;
                    case 2 -> 1;
                    default -> 0;
                });
                states.set(idx, rotated);
            } else if (type == DIRECTION) {
                int rotatedDirection = block instanceof BlockTrapdoor
                        ? rotateTrapdoorDirectionClockwise90(state.getPropertyValue(DIRECTION))
                        : (state.getPropertyValue(DIRECTION) + 1) % 4;
                var rotated = DIRECTION.createValue(rotatedDirection);
                states.set(idx, rotated);
            } else if (type == GROUND_SIGN_DIRECTION) {
                var rotated = GROUND_SIGN_DIRECTION.createValue((state.getPropertyValue(GROUND_SIGN_DIRECTION) + 4) % 16);
                states.set(idx, rotated);
            } else if (type == FACING_DIRECTION) {
                int rotatedFacing = block instanceof BlockJigsaw
                        ? rotateFacingDirectionCounterclockwise90(state.getPropertyValue(FACING_DIRECTION))
                        : rotateFacingDirectionClockwise90(state.getPropertyValue(FACING_DIRECTION));
                var rotated = FACING_DIRECTION.createValue(rotatedFacing);
                states.set(idx, rotated);
            } else if (type == ROTATION && block instanceof BlockJigsaw) {
                var rotated = ROTATION.createValue(rotateJigsawRotationClockwise90(state.getPropertyValue(ROTATION)));
                states.set(idx, rotated);
            } else if (type == LEVER_DIRECTION) {
                int meta = state.getPropertyValue(LEVER_DIRECTION).getMetadata();
                int thrown = meta & 0x8;
                var rotated = LEVER_DIRECTION.createValue(LeverDirection.byMetadata(switch (meta & ~0x8) {
                    case 1 -> 3 | thrown;
                    case 2 -> 4 | thrown;
                    case 3 -> 2 | thrown;
                    case 4 -> 1 | thrown;
                    case 5 -> 6 | thrown;
                    case 6 -> 5 | thrown;
                    case 7 -> thrown;
                    default -> 7 | thrown;
                }));
                states.set(idx, rotated);
            } else if (type == MINECRAFT_CARDINAL_DIRECTION) {
                var rotated = MINECRAFT_CARDINAL_DIRECTION.createValue(switch (state.getPropertyValue(MINECRAFT_CARDINAL_DIRECTION)) {
                    case NORTH -> MinecraftCardinalDirection.EAST;
                    case EAST -> MinecraftCardinalDirection.SOUTH;
                    case SOUTH -> MinecraftCardinalDirection.WEST;
                    case WEST -> MinecraftCardinalDirection.NORTH;
                });
                states.set(idx, rotated);
            } else if (type == PILLAR_AXIS) {
                var rotated = PILLAR_AXIS.createValue(switch (state.getPropertyValue(PILLAR_AXIS)) {
                    case X -> BlockFace.Axis.Z;
                    case Z -> BlockFace.Axis.X;
                    default -> BlockFace.Axis.Y;
                });
                states.set(idx, rotated);
            } else if (type == HUGE_MUSHROOM_BITS) {
                int meta = state.getPropertyValue(HUGE_MUSHROOM_BITS);
                if (meta <= 10) meta = (meta * 3) % 10;
                var rotated = HUGE_MUSHROOM_BITS.createValue(meta);
                states.set(idx, rotated);
            } else if (type == VINE_DIRECTION_BITS) {
                int meta = state.getPropertyValue(VINE_DIRECTION_BITS);
                meta = ((meta << 1) | (meta >> 3)) & 0xf;
                var rotated = VINE_DIRECTION_BITS.createValue(meta);
                states.set(idx, rotated);
            } else {
                if (rotatedWallConnections != null) {
                    WallConnectionType rotatedConnection = getRotatedWallConnection(type, rotatedWallConnections);
                    if (rotatedConnection != null) {
                        @SuppressWarnings("unchecked")
                        var rotated = ((EnumPropertyType<WallConnectionType>) type).createValue(rotatedConnection);
                        states.set(idx, rotated);
                    }
                }
            }
            idx++;
        }
        return state.setPropertyValues(block.getProperties(), states.toArray(BlockPropertyType.BlockPropertyValue[]::new));
    }

    private static EnumMap<BlockFace, WallConnectionType> rotateWallConnectionsClockwise90(BlockState state) {
        EnumMap<BlockFace, WallConnectionType> rotatedConnections = new EnumMap<>(BlockFace.class);
        putRotatedWallConnection(rotatedConnections, BlockFace.EAST, state.getPropertyValue(WALL_CONNECTION_TYPE_NORTH));
        putRotatedWallConnection(rotatedConnections, BlockFace.NORTH, state.getPropertyValue(WALL_CONNECTION_TYPE_WEST));
        putRotatedWallConnection(rotatedConnections, BlockFace.SOUTH, state.getPropertyValue(WALL_CONNECTION_TYPE_EAST));
        putRotatedWallConnection(rotatedConnections, BlockFace.WEST, state.getPropertyValue(WALL_CONNECTION_TYPE_SOUTH));
        return rotatedConnections;
    }

    private static void putRotatedWallConnection(EnumMap<BlockFace, WallConnectionType> rotatedConnections, BlockFace face, WallConnectionType connectionType) {
        if (connectionType != null) {
            rotatedConnections.put(face, connectionType);
        }
    }

    private static WallConnectionType getRotatedWallConnection(BlockPropertyType<?> type, EnumMap<BlockFace, WallConnectionType> rotatedWallConnections) {
        if (type == WALL_CONNECTION_TYPE_EAST) {
            return rotatedWallConnections.get(BlockFace.EAST);
        }
        if (type == WALL_CONNECTION_TYPE_NORTH) {
            return rotatedWallConnections.get(BlockFace.NORTH);
        }
        if (type == WALL_CONNECTION_TYPE_SOUTH) {
            return rotatedWallConnections.get(BlockFace.SOUTH);
        }
        if (type == WALL_CONNECTION_TYPE_WEST) {
            return rotatedWallConnections.get(BlockFace.WEST);
        }
        return null;
    }

    private static int rotateFacingDirectionClockwise90(int meta) {
        int extraBits = meta & ~0x7;
        BlockFace face = BlockFace.fromIndex(meta & 0x7);
        if (!face.getAxis().isHorizontal()) {
            return face.getIndex() | extraBits;
        }
        return face.rotateY().getIndex() | extraBits;
    }

    private static int rotateTrapdoorDirectionClockwise90(int value) {
        BlockFace face = CommonPropertyMap.EWSN_DIRECTION.inverse().get(value);
        if (face == null) {
            return value;
        }
        return CommonPropertyMap.EWSN_DIRECTION.get(face.rotateY());
    }

    private static int rotateFacingDirectionCounterclockwise90(int meta) {
        int extraBits = meta & ~0x7;
        BlockFace face = BlockFace.fromIndex(meta & 0x7);
        if (!face.getAxis().isHorizontal()) {
            return face.getIndex() | extraBits;
        }
        return face.rotateYCCW().getIndex() | extraBits;
    }

    private static int rotateJigsawRotationClockwise90(int value) {
        return switch (value & 0x3) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            default -> 0;
        };
    }

    public static BlockState counterclockwise90(BlockState state) {
        BlockState result = state;
        for (int i = 0; i < 3; i++) result = clockwise90(result);
        return result;
    }

    public static BlockState clockwise180(BlockState state) {
        BlockState result = state;
        for (int i = 0; i < 2; i++) result = clockwise90(result);
        return result;
    }

    /**
     * Mirror a block state across the X axis (i.e. swap its east and west facing while keeping
     * north/south). This reverses orientation, so chiral properties such as door hinges are flipped.
     */
    public static BlockState mirrorX(BlockState state) {
        Block block = state.toBlock();
        var states = new java.util.ArrayList<>(state.getBlockPropertyValues());
        EnumMap<BlockFace, WallConnectionType> mirroredWallConnections = null;
        if (state.getPropertyValue(WALL_POST_BIT) != null) {
            mirroredWallConnections = mirrorWallConnectionsX(state);
        }

        int idx = 0;
        for (var property : state.getBlockPropertyValues()) {
            var type = property.getPropertyType();

            if (type == TORCH_FACING_DIRECTION) {
                var mirrored = TORCH_FACING_DIRECTION.createValue(switch (state.getPropertyValue(TORCH_FACING_DIRECTION)) {
                    case EAST -> WEST;
                    case WEST -> EAST;
                    default -> state.getPropertyValue(TORCH_FACING_DIRECTION);
                });
                states.set(idx, mirrored);
            } else if (type == RAIL_DIRECTION_10) {
                var mirrored = RAIL_DIRECTION_10.createValue(switch (state.getPropertyValue(RAIL_DIRECTION_10)) {
                    case 2 -> 3;
                    case 3 -> 2;
                    case 6 -> 7;
                    case 7 -> 6;
                    case 8 -> 9;
                    case 9 -> 8;
                    default -> state.getPropertyValue(RAIL_DIRECTION_10);
                });
                states.set(idx, mirrored);
            } else if (type == RAIL_DIRECTION_6) {
                var mirrored = RAIL_DIRECTION_6.createValue(switch (state.getPropertyValue(RAIL_DIRECTION_6)) {
                    case 2 -> 3;
                    case 3 -> 2;
                    default -> state.getPropertyValue(RAIL_DIRECTION_6);
                });
                states.set(idx, mirrored);
            } else if (type == WEIRDO_DIRECTION) {
                var mirrored = WEIRDO_DIRECTION.createValue(switch (state.getPropertyValue(WEIRDO_DIRECTION)) {
                    case 0 -> 1;
                    case 1 -> 0;
                    default -> state.getPropertyValue(WEIRDO_DIRECTION);
                });
                states.set(idx, mirrored);
            } else if (type == DIRECTION) {
                int mirroredDirection = block instanceof BlockTrapdoor
                        ? mirrorTrapdoorDirectionX(state.getPropertyValue(DIRECTION))
                        : mirrorDirectionX(state.getPropertyValue(DIRECTION));
                states.set(idx, DIRECTION.createValue(mirroredDirection));
            } else if (type == GROUND_SIGN_DIRECTION) {
                var mirrored = GROUND_SIGN_DIRECTION.createValue((16 - state.getPropertyValue(GROUND_SIGN_DIRECTION)) % 16);
                states.set(idx, mirrored);
            } else if (type == FACING_DIRECTION) {
                states.set(idx, FACING_DIRECTION.createValue(mirrorFacingDirectionX(state.getPropertyValue(FACING_DIRECTION))));
            } else if (type == ROTATION && block instanceof BlockJigsaw) {
                var mirrored = ROTATION.createValue((4 - (state.getPropertyValue(ROTATION) & 0x3)) & 0x3);
                states.set(idx, mirrored);
            } else if (type == LEVER_DIRECTION) {
                int meta = state.getPropertyValue(LEVER_DIRECTION).getMetadata();
                var mirrored = LEVER_DIRECTION.createValue(LeverDirection.byMetadata(switch (meta) {
                    case 1 -> 2;
                    case 2 -> 1;
                    default -> meta;
                }));
                states.set(idx, mirrored);
            } else if (type == MINECRAFT_CARDINAL_DIRECTION) {
                var mirrored = MINECRAFT_CARDINAL_DIRECTION.createValue(switch (state.getPropertyValue(MINECRAFT_CARDINAL_DIRECTION)) {
                    case EAST -> MinecraftCardinalDirection.WEST;
                    case WEST -> MinecraftCardinalDirection.EAST;
                    default -> state.getPropertyValue(MINECRAFT_CARDINAL_DIRECTION);
                });
                states.set(idx, mirrored);
            } else if (type == VINE_DIRECTION_BITS) {
                int meta = state.getPropertyValue(VINE_DIRECTION_BITS);
                int mirrored = (meta & 0x1) | (meta & 0x4) | ((meta & 0x2) << 2) | ((meta & 0x8) >> 2);
                states.set(idx, VINE_DIRECTION_BITS.createValue(mirrored));
            } else if (type == DOOR_HINGE_BIT) {
                states.set(idx, DOOR_HINGE_BIT.createValue(!state.getPropertyValue(DOOR_HINGE_BIT)));
            } else {
                if (mirroredWallConnections != null) {
                    WallConnectionType mirroredConnection = getRotatedWallConnection(type, mirroredWallConnections);
                    if (mirroredConnection != null) {
                        @SuppressWarnings("unchecked")
                        var mirrored = ((EnumPropertyType<WallConnectionType>) type).createValue(mirroredConnection);
                        states.set(idx, mirrored);
                    }
                }
            }
            idx++;
        }
        return state.setPropertyValues(block.getProperties(), states.toArray(BlockPropertyType.BlockPropertyValue[]::new));
    }

    /**
     * Mirror a block state across the Z axis (swap north and south facing while keeping east/west).
     * Expressed as a 180° rotation of the X mirror, since two perpendicular reflections compose into
     * a half-turn - this reuses the rotation logic and keeps chirality correct.
     */
    public static BlockState mirrorZ(BlockState state) {
        return clockwise180(mirrorX(state));
    }

    private static EnumMap<BlockFace, WallConnectionType> mirrorWallConnectionsX(BlockState state) {
        EnumMap<BlockFace, WallConnectionType> mirroredConnections = new EnumMap<>(BlockFace.class);
        putRotatedWallConnection(mirroredConnections, BlockFace.EAST, state.getPropertyValue(WALL_CONNECTION_TYPE_WEST));
        putRotatedWallConnection(mirroredConnections, BlockFace.WEST, state.getPropertyValue(WALL_CONNECTION_TYPE_EAST));
        putRotatedWallConnection(mirroredConnections, BlockFace.NORTH, state.getPropertyValue(WALL_CONNECTION_TYPE_NORTH));
        putRotatedWallConnection(mirroredConnections, BlockFace.SOUTH, state.getPropertyValue(WALL_CONNECTION_TYPE_SOUTH));
        return mirroredConnections;
    }

    private static BlockFace mirrorFaceX(BlockFace face) {
        return face.getAxis() == BlockFace.Axis.X ? face.getOpposite() : face;
    }

    private static int mirrorDirectionX(int value) {
        return mirrorFaceX(BlockFace.fromHorizontalIndex(value)).getHorizontalIndex();
    }

    private static int mirrorTrapdoorDirectionX(int value) {
        BlockFace face = CommonPropertyMap.EWSN_DIRECTION.inverse().get(value);
        if (face == null) {
            return value;
        }
        return CommonPropertyMap.EWSN_DIRECTION.get(mirrorFaceX(face));
    }

    private static int mirrorFacingDirectionX(int meta) {
        int extraBits = meta & ~0x7;
        return mirrorFaceX(BlockFace.fromIndex(meta & 0x7)).getIndex() | extraBits;
    }

    public static Rotation rotateBy(Rotation self, Rotation other) {
        return Rotation.values()[(self.ordinal() + other.ordinal()) & 0x3];
    }
}
