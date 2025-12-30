package cn.nukkit.network.protocol.types;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.enums.LeverDirection;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.block.property.type.BlockPropertyType;
import cn.nukkit.math.BlockFace;

import static cn.nukkit.block.property.CommonBlockProperties.*;
import static cn.nukkit.block.property.CommonBlockProperties.HUGE_MUSHROOM_BITS;
import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;
import static cn.nukkit.block.property.CommonBlockProperties.VINE_DIRECTION_BITS;
import static cn.nukkit.block.property.enums.TorchFacingDirection.EAST;
import static cn.nukkit.block.property.enums.TorchFacingDirection.NORTH;
import static cn.nukkit.block.property.enums.TorchFacingDirection.SOUTH;
import static cn.nukkit.block.property.enums.TorchFacingDirection.UNKNOWN;
import static cn.nukkit.block.property.enums.TorchFacingDirection.WEST;

public enum Rotation {
    NONE,
    ROTATE_90,
    ROTATE_180,
    ROTATE_270;

    private static final Rotation[] VALUES = Rotation.values();

    public static Rotation from(int id) {
        return VALUES[id];
    }

    public static BlockState clockwise90(BlockState state) {
        Block block = state.toBlock();
        var states = new java.util.ArrayList<>(state.getBlockPropertyValues());
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
                var rotated = DIRECTION.createValue((state.getPropertyValue(DIRECTION) + 1) % 4);
                states.set(idx, rotated);
            } else if (type == GROUND_SIGN_DIRECTION) {
                var rotated = GROUND_SIGN_DIRECTION.createValue((state.getPropertyValue(GROUND_SIGN_DIRECTION) + 4) % 16);
                states.set(idx, rotated);
            } else if (type == FACING_DIRECTION) {
                int meta = state.getPropertyValue(FACING_DIRECTION);
                int thrown = meta & 0x8;
                var rotated = FACING_DIRECTION.createValue(switch (meta & ~0x8) {
                    case 2 -> 5 | thrown;
                    case 3 -> 4 | thrown;
                    case 4 -> 2 | thrown;
                    default -> 3 | thrown;
                });
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
            } else if(type == PILLAR_AXIS) {
                var rotated = PILLAR_AXIS.createValue(switch (state.getPropertyValue(PILLAR_AXIS)) {
                    case X -> BlockFace.Axis.Z;
                    case Z -> BlockFace.Axis.X;
                    default -> BlockFace.Axis.Y;
                });
                states.set(idx, rotated);
            } else if(type == HUGE_MUSHROOM_BITS) {
                int meta = state.getPropertyValue(HUGE_MUSHROOM_BITS);
                if(meta <= 10) meta = (meta * 3) % 10;
                var rotated = HUGE_MUSHROOM_BITS.createValue(meta);
                states.set(idx, rotated);
            } else if(type == VINE_DIRECTION_BITS) {
                int meta = state.getPropertyValue(VINE_DIRECTION_BITS);
                meta = ((meta << 1) | (meta >> 3)) & 0xf;
                var rotated = VINE_DIRECTION_BITS.createValue(meta);
                states.set(idx, rotated);
            }
            idx++;
        }
        return state.setPropertyValues(block.getProperties(), states.toArray(BlockPropertyType.BlockPropertyValue[]::new));
    }

    public static BlockState counterclockwise90(BlockState state) {
        BlockState result = state;
        for(int i = 0; i < 3; i++) result = clockwise90(result);
        return result;
    }

    public static BlockState clockwise180(BlockState state) {
        BlockState result = state;
        for(int i = 0; i < 2; i++) result = clockwise90(result);
        return result;
    }

}
