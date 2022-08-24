package cn.nukkit.level.generator.populator.impl.structure.utils.math;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;

//todo: 更改为基于BlockProperty的实现
@PowerNukkitXOnly
@Since("1.19.21-r2")
public enum Rotation {
    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    COUNTERCLOCKWISE_90;

    /**
     * Rotate a block's data value 90 degrees (north->east->south->west->north);
     *
     * @param id   the ID of the bock
     * @param meta the meta of the block
     * @return the new meta
     */
    public static int clockwise90(int id, int meta) {
        switch (id) {
            case BlockID.TORCH:
            case BlockID.UNLIT_REDSTONE_TORCH:
            case BlockID.REDSTONE_TORCH:
                switch (meta) {
                    case 1:
                        return 3;
                    case 2:
                        return 4;
                    case 3:
                        return 2;
                    case 4:
                        return 1;
                    // 5 is vertical
                }
                break;

            case BlockID.RAIL:
                switch (meta) {
                    case 6:
                        return 7;
                    case 7:
                        return 8;
                    case 8:
                        return 9;
                    case 9:
                        return 6;
                }
                /* FALL-THROUGH */

            case BlockID.POWERED_RAIL:
            case BlockID.DETECTOR_RAIL:
            case BlockID.ACTIVATOR_RAIL:
                switch (meta & 0x7) {
                    case 0:
                        return 1 | (meta & ~0x7);
                    case 1:
                        return 0 | (meta & ~0x7);
                    case 2:
                        return 5 | (meta & ~0x7);
                    case 3:
                        return 4 | (meta & ~0x7);
                    case 4:
                        return 2 | (meta & ~0x7);
                    case 5:
                        return 3 | (meta & ~0x7);
                }
                break;

            case BlockID.RED_SANDSTONE_STAIRS:
            case BlockID.OAK_WOOD_STAIRS:
            case BlockID.COBBLESTONE_STAIRS:
            case BlockID.BRICK_STAIRS:
            case BlockID.STONE_BRICK_STAIRS:
            case BlockID.NETHER_BRICKS_STAIRS:
            case BlockID.SANDSTONE_STAIRS:
            case BlockID.SPRUCE_WOOD_STAIRS:
            case BlockID.BIRCH_WOOD_STAIRS:
            case BlockID.JUNGLE_WOOD_STAIRS:
            case BlockID.QUARTZ_STAIRS:
            case BlockID.ACACIA_WOODEN_STAIRS:
            case BlockID.DARK_OAK_WOODEN_STAIRS:
            case BlockID.PURPUR_STAIRS:
                switch (meta) {
                    case 0:
                        return 2;
                    case 1:
                        return 3;
                    case 2:
                        return 1;
                    case 3:
                        return 0;
                    case 4:
                        return 6;
                    case 5:
                        return 7;
                    case 6:
                        return 5;
                    case 7:
                        return 4;
                }
                break;

            case BlockID.STONE_BUTTON:
            case BlockID.WOODEN_BUTTON: {
                int thrown = meta & 0x8;
                switch (meta & ~0x8) {
                    case 2:
                        return 5 | thrown;
                    case 3:
                        return 4 | thrown;
                    case 4:
                        return 2 | thrown;
                    case 5:
                        return 3 | thrown;
                    // 0 and 1 are vertical
                }
                break;
            }

            case BlockID.LEVER: {
                int thrown = meta & 0x8;
                switch (meta & ~0x8) {
                    case 1:
                        return 3 | thrown;
                    case 2:
                        return 4 | thrown;
                    case 3:
                        return 2 | thrown;
                    case 4:
                        return 1 | thrown;
                    case 5:
                        return 6 | thrown;
                    case 6:
                        return 5 | thrown;
                    case 7:
                        return 0 | thrown;
                    case 0:
                        return 7 | thrown;
                }
                break;
            }

            case BlockID.WOODEN_DOOR_BLOCK:
            case BlockID.IRON_DOOR_BLOCK:
            case BlockID.SPRUCE_DOOR_BLOCK:
            case BlockID.BIRCH_DOOR_BLOCK:
            case BlockID.JUNGLE_DOOR_BLOCK:
            case BlockID.ACACIA_DOOR_BLOCK:
            case BlockID.DARK_OAK_DOOR_BLOCK:
                if ((meta & 0x8) != 0) {
                    // door top halves contain no orientation information
                    break;
                }

                /* FALL-THROUGH */

            case BlockID.END_PORTAL_FRAME:
            case BlockID.COCOA_BLOCK:
            case BlockID.TRIPWIRE_HOOK: {
                int extra = meta & ~0x3;
                int withoutFlags = meta & 0x3;
                switch (withoutFlags) {
                    case 0:
                        return 1 | extra;
                    case 1:
                        return 2 | extra;
                    case 2:
                        return 3 | extra;
                    case 3:
                        return 0 | extra;
                }
                break;
            }
            case BlockID.SIGN_POST:
            case BlockID.STANDING_BANNER:
                return (meta + 4) % 16;

            case BlockID.LADDER:
            case BlockID.WALL_SIGN:
            case BlockID.WALL_BANNER:
            case BlockID.CHEST:
            case BlockID.FURNACE:
            case BlockID.BURNING_FURNACE:
            case BlockID.ENDER_CHEST:
            case BlockID.TRAPPED_CHEST:
            case BlockID.HOPPER_BLOCK: {
                int extra = meta & 0x8;
                int withoutFlags = meta & ~0x8;
                switch (withoutFlags) {
                    case 2:
                        return 5 | extra;
                    case 3:
                        return 4 | extra;
                    case 4:
                        return 2 | extra;
                    case 5:
                        return 3 | extra;
                }
                break;
            }
            case BlockID.DISPENSER:
            case BlockID.DROPPER:
            case BlockID.END_ROD:
                int dispPower = meta & 0x8;
                switch (meta & ~0x8) {
                    case 2:
                        return 5 | dispPower;
                    case 3:
                        return 4 | dispPower;
                    case 4:
                        return 2 | dispPower;
                    case 5:
                        return 3 | dispPower;
                }
                break;

            case BlockID.PUMPKIN:
            case BlockID.JACK_O_LANTERN:
                switch (meta) {
                    case 0:
                        return 1;
                    case 1:
                        return 2;
                    case 2:
                        return 3;
                    case 3:
                        return 0;
                }
                break;

            case BlockID.HAY_BALE:
            case BlockID.LOG:
            case BlockID.LOG2:
            case BlockID.QUARTZ_BLOCK:
            case BlockID.PURPUR_BLOCK:
            case BlockID.BONE_BLOCK:
                if (meta >= 4 && meta <= 11) meta ^= 0xc;
                break;

            case BlockID.UNPOWERED_COMPARATOR:
            case BlockID.POWERED_COMPARATOR:
            case BlockID.UNPOWERED_REPEATER:
            case BlockID.POWERED_REPEATER:
                int dir = meta & 0x03;
                int delay = meta - dir;
                switch (dir) {
                    case 0:
                        return 1 | delay;
                    case 1:
                        return 2 | delay;
                    case 2:
                        return 3 | delay;
                    case 3:
                        return 0 | delay;
                }
                break;

            case BlockID.TRAPDOOR:
            case BlockID.IRON_TRAPDOOR:
                int withoutOrientation = meta & ~0x3;
                int orientation = meta & 0x3;
                switch (orientation) {
                    case 0:
                        return 3 | withoutOrientation;
                    case 1:
                        return 2 | withoutOrientation;
                    case 2:
                        return 0 | withoutOrientation;
                    case 3:
                        return 1 | withoutOrientation;
                }
                break;

            case BlockID.PISTON:
            case BlockID.STICKY_PISTON:
            case BlockID.PISTON_HEAD_STICKY:
            case 137: //BlockID.COMMAND_BLOCK
            case 188: //BlockID.REPEATING_COMMAND_BLOCK
            case 189: //BlockID.CHAIN_COMMAND_BLOCK
                final int rest = meta & ~0x7;
                switch (meta & 0x7) {
                    case 2:
                        return 5 | rest;
                    case 3:
                        return 4 | rest;
                    case 4:
                        return 2 | rest;
                    case 5:
                        return 3 | rest;
                }
                break;

            case BlockID.BROWN_MUSHROOM_BLOCK:
            case BlockID.RED_MUSHROOM_BLOCK:
                if (meta >= 10) return meta;
                return (meta * 3) % 10;

            case BlockID.VINE:
                return ((meta << 1) | (meta >> 3)) & 0xf;

            case BlockID.FENCE_GATE:
            case BlockID.FENCE_GATE_SPRUCE:
            case BlockID.FENCE_GATE_BIRCH:
            case BlockID.FENCE_GATE_JUNGLE:
            case BlockID.FENCE_GATE_DARK_OAK:
            case BlockID.FENCE_GATE_ACACIA:
                return ((meta + 1) & 0x3) | (meta & ~0x3);

            case BlockID.ANVIL:
                int damage = meta & ~0x3;
                switch (meta & 0x3) {
                    case 0:
                        return 3 | damage;
                    case 2:
                        return 1 | damage;
                    case 1:
                        return 0 | damage;
                    case 3:
                        return 2 | damage;
                }
                break;

            case BlockID.BED_BLOCK:
                return meta & ~0x3 | (meta + 1) & 0x3;

            case BlockID.SKULL_BLOCK:
            case BlockID.PURPLE_GLAZED_TERRACOTTA:
            case BlockID.WHITE_GLAZED_TERRACOTTA:
            case BlockID.ORANGE_GLAZED_TERRACOTTA:
            case BlockID.MAGENTA_GLAZED_TERRACOTTA:
            case BlockID.LIGHT_BLUE_GLAZED_TERRACOTTA:
            case BlockID.YELLOW_GLAZED_TERRACOTTA:
            case BlockID.LIME_GLAZED_TERRACOTTA:
            case BlockID.PINK_GLAZED_TERRACOTTA:
            case BlockID.GRAY_GLAZED_TERRACOTTA:
            case BlockID.SILVER_GLAZED_TERRACOTTA:
            case BlockID.CYAN_GLAZED_TERRACOTTA:
            case BlockID.BLUE_GLAZED_TERRACOTTA:
            case BlockID.BROWN_GLAZED_TERRACOTTA:
            case BlockID.GREEN_GLAZED_TERRACOTTA:
            case BlockID.RED_GLAZED_TERRACOTTA:
            case BlockID.BLACK_GLAZED_TERRACOTTA:
            case BlockID.OBSERVER:
                switch (meta) {
                    case 2:
                        return 5;
                    case 3:
                        return 4;
                    case 4:
                        return 2;
                    case 5:
                        return 3;
                }
                break;

            case BlockID.NETHER_PORTAL:
                return (meta + 1) & 0x1;

            case BlockID.ITEM_FRAME_BLOCK:
                switch (meta) {
                    case 0:
                        return 2;
                    case 1:
                        return 3;
                    case 2:
                        return 1;
                    case 3:
                        return 0;
                }
                break;

        }

        return meta;
    }

    /**
     * Rotate a block's data value -90 degrees (north<-east<-south<-west<-north);
     *
     * @param id   the ID of the bock
     * @param meta the meta of the block
     * @return the new meta
     */
    public static int counterclockwise90(int id, int meta) {
        switch (id) {
            case BlockID.TORCH:
            case BlockID.UNLIT_REDSTONE_TORCH:
            case BlockID.REDSTONE_TORCH:
                switch (meta) {
                    case 3:
                        return 1;
                    case 4:
                        return 2;
                    case 2:
                        return 3;
                    case 1:
                        return 4;
                    // 5 is vertical
                }
                break;

            case BlockID.RAIL:
                switch (meta) {
                    case 7:
                        return 6;
                    case 8:
                        return 7;
                    case 9:
                        return 8;
                    case 6:
                        return 9;
                }
                /* FALL-THROUGH */

            case BlockID.POWERED_RAIL:
            case BlockID.DETECTOR_RAIL:
            case BlockID.ACTIVATOR_RAIL:
                int power = meta & ~0x7;
                switch (meta & 0x7) {
                    case 1:
                        return 0 | power;
                    case 0:
                        return 1 | power;
                    case 5:
                        return 2 | power;
                    case 4:
                        return 3 | power;
                    case 2:
                        return 4 | power;
                    case 3:
                        return 5 | power;
                }
                break;

            case BlockID.RED_SANDSTONE_STAIRS:
            case BlockID.OAK_WOOD_STAIRS:
            case BlockID.COBBLESTONE_STAIRS:
            case BlockID.BRICK_STAIRS:
            case BlockID.STONE_BRICK_STAIRS:
            case BlockID.NETHER_BRICKS_STAIRS:
            case BlockID.SANDSTONE_STAIRS:
            case BlockID.SPRUCE_WOOD_STAIRS:
            case BlockID.BIRCH_WOOD_STAIRS:
            case BlockID.JUNGLE_WOOD_STAIRS:
            case BlockID.QUARTZ_STAIRS:
            case BlockID.ACACIA_WOODEN_STAIRS:
            case BlockID.DARK_OAK_WOODEN_STAIRS:
            case BlockID.PURPUR_STAIRS:
                switch (meta) {
                    case 2:
                        return 0;
                    case 3:
                        return 1;
                    case 1:
                        return 2;
                    case 0:
                        return 3;
                    case 6:
                        return 4;
                    case 7:
                        return 5;
                    case 5:
                        return 6;
                    case 4:
                        return 7;
                }
                break;

            case BlockID.STONE_BUTTON:
            case BlockID.WOODEN_BUTTON: {
                int thrown = meta & 0x8;
                switch (meta & ~0x8) {
                    case 4:
                        return 3 | thrown;
                    case 5:
                        return 2 | thrown;
                    case 3:
                        return 5 | thrown;
                    case 2:
                        return 4 | thrown;
                    // 0 and 1 are vertical
                }
                break;
            }

            case BlockID.LEVER: {
                int thrown = meta & 0x8;
                switch (meta & ~0x8) {
                    case 3:
                        return 1 | thrown;
                    case 4:
                        return 2 | thrown;
                    case 2:
                        return 3 | thrown;
                    case 1:
                        return 4 | thrown;
                    case 6:
                        return 5 | thrown;
                    case 5:
                        return 6 | thrown;
                    case 0:
                        return 7 | thrown;
                    case 7:
                        return 0 | thrown;
                }
                break;
            }

            case BlockID.WOODEN_DOOR_BLOCK:
            case BlockID.IRON_DOOR_BLOCK:
            case BlockID.SPRUCE_DOOR_BLOCK:
            case BlockID.BIRCH_DOOR_BLOCK:
            case BlockID.JUNGLE_DOOR_BLOCK:
            case BlockID.ACACIA_DOOR_BLOCK:
            case BlockID.DARK_OAK_DOOR_BLOCK:
                if ((meta & 0x8) != 0) {
                    // door top halves contain no orientation information
                    break;
                }

                /* FALL-THROUGH */

            case BlockID.END_PORTAL_FRAME:
            case BlockID.COCOA_BLOCK:
            case BlockID.TRIPWIRE_HOOK: {
                int extra = meta & ~0x3;
                int withoutFlags = meta & 0x3;
                switch (withoutFlags) {
                    case 1:
                        return 0 | extra;
                    case 2:
                        return 1 | extra;
                    case 3:
                        return 2 | extra;
                    case 0:
                        return 3 | extra;
                }
                break;
            }
            case BlockID.SIGN_POST:
            case BlockID.STANDING_BANNER:
                return (meta + 12) % 16;

            case BlockID.LADDER:
            case BlockID.WALL_SIGN:
            case BlockID.WALL_BANNER:
            case BlockID.CHEST:
            case BlockID.FURNACE:
            case BlockID.BURNING_FURNACE:
            case BlockID.ENDER_CHEST:
            case BlockID.TRAPPED_CHEST:
            case BlockID.HOPPER_BLOCK: {
                int extra = meta & 0x8;
                int withoutFlags = meta & ~0x8;
                switch (withoutFlags) {
                    case 5:
                        return 2 | extra;
                    case 4:
                        return 3 | extra;
                    case 2:
                        return 4 | extra;
                    case 3:
                        return 5 | extra;
                }
                break;
            }
            case BlockID.DISPENSER:
            case BlockID.DROPPER:
            case BlockID.END_ROD:
                int dispPower = meta & 0x8;
                switch (meta & ~0x8) {
                    case 5:
                        return 2 | dispPower;
                    case 4:
                        return 3 | dispPower;
                    case 2:
                        return 4 | dispPower;
                    case 3:
                        return 5 | dispPower;
                }
                break;
            case BlockID.PUMPKIN:
            case BlockID.JACK_O_LANTERN:
                switch (meta) {
                    case 1:
                        return 0;
                    case 2:
                        return 1;
                    case 3:
                        return 2;
                    case 0:
                        return 3;
                }
                break;
            case BlockID.HAY_BALE:
            case BlockID.LOG:
            case BlockID.LOG2:
            case BlockID.QUARTZ_BLOCK:
            case BlockID.PURPUR_BLOCK:
            case BlockID.BONE_BLOCK:
                if (meta >= 4 && meta <= 11) meta ^= 0xc;
                break;

            case BlockID.UNPOWERED_COMPARATOR:
            case BlockID.POWERED_COMPARATOR:
            case BlockID.UNPOWERED_REPEATER:
            case BlockID.POWERED_REPEATER:
                int dir = meta & 0x03;
                int delay = meta - dir;
                switch (dir) {
                    case 1:
                        return 0 | delay;
                    case 2:
                        return 1 | delay;
                    case 3:
                        return 2 | delay;
                    case 0:
                        return 3 | delay;
                }
                break;

            case BlockID.TRAPDOOR:
            case BlockID.IRON_TRAPDOOR:
                int withoutOrientation = meta & ~0x3;
                int orientation = meta & 0x3;
                switch (orientation) {
                    case 3:
                        return 0 | withoutOrientation;
                    case 2:
                        return 1 | withoutOrientation;
                    case 0:
                        return 2 | withoutOrientation;
                    case 1:
                        return 3 | withoutOrientation;
                }

            case BlockID.PISTON:
            case BlockID.STICKY_PISTON:
            case BlockID.PISTON_HEAD_STICKY:
            case 137: //BlockID.COMMAND_BLOCK
            case 188: //BlockID.REPEATING_COMMAND_BLOCK
            case 189: //BlockID.CHAIN_COMMAND_BLOCK
                final int rest = meta & ~0x7;
                switch (meta & 0x7) {
                    case 5:
                        return 2 | rest;
                    case 4:
                        return 3 | rest;
                    case 2:
                        return 4 | rest;
                    case 3:
                        return 5 | rest;
                }
                break;

            case BlockID.BROWN_MUSHROOM_BLOCK:
            case BlockID.RED_MUSHROOM_BLOCK:
                if (meta >= 10) return meta;
                return (meta * 7) % 10;

            case BlockID.VINE:
                return ((meta >> 1) | (meta << 3)) & 0xf;

            case BlockID.FENCE_GATE:
            case BlockID.FENCE_GATE_SPRUCE:
            case BlockID.FENCE_GATE_BIRCH:
            case BlockID.FENCE_GATE_JUNGLE:
            case BlockID.FENCE_GATE_DARK_OAK:
            case BlockID.FENCE_GATE_ACACIA:
                return ((meta + 3) & 0x3) | (meta & ~0x3);

            case BlockID.ANVIL:
                int damage = meta & ~0x3;
                switch (meta & 0x3) {
                    case 0:
                        return 1 | damage;
                    case 2:
                        return 3 | damage;
                    case 1:
                        return 2 | damage;
                    case 3:
                        return 0 | damage;
                }
                break;

            case BlockID.BED_BLOCK:
                return meta & ~0x3 | (meta - 1) & 0x3;

            case BlockID.SKULL_BLOCK:
            case BlockID.PURPLE_GLAZED_TERRACOTTA:
            case BlockID.WHITE_GLAZED_TERRACOTTA:
            case BlockID.ORANGE_GLAZED_TERRACOTTA:
            case BlockID.MAGENTA_GLAZED_TERRACOTTA:
            case BlockID.LIGHT_BLUE_GLAZED_TERRACOTTA:
            case BlockID.YELLOW_GLAZED_TERRACOTTA:
            case BlockID.LIME_GLAZED_TERRACOTTA:
            case BlockID.PINK_GLAZED_TERRACOTTA:
            case BlockID.GRAY_GLAZED_TERRACOTTA:
            case BlockID.SILVER_GLAZED_TERRACOTTA:
            case BlockID.CYAN_GLAZED_TERRACOTTA:
            case BlockID.BLUE_GLAZED_TERRACOTTA:
            case BlockID.BROWN_GLAZED_TERRACOTTA:
            case BlockID.GREEN_GLAZED_TERRACOTTA:
            case BlockID.RED_GLAZED_TERRACOTTA:
            case BlockID.BLACK_GLAZED_TERRACOTTA:
            case BlockID.OBSERVER:
                switch (meta) {
                    case 2:
                        return 4;
                    case 3:
                        return 5;
                    case 4:
                        return 3;
                    case 5:
                        return 2;
                }
                break;

            case BlockID.NETHER_PORTAL:
                return (meta + 1) & 0x1;

            case BlockID.ITEM_FRAME_BLOCK:
                switch (meta) {
                    case 0:
                        return 3;
                    case 1:
                        return 2;
                    case 2:
                        return 0;
                    case 3:
                        return 1;
                }
                break;

        }

        return meta;
    }

    /**
     * Flip a block's data value.
     *
     * @param id   the ID of the bock
     * @param meta the meta of the block
     * @return the new meta
     */
    public static int clockwise180(int id, int meta) {
        switch (id) {
            case BlockID.TORCH:
            case BlockID.UNLIT_REDSTONE_TORCH:
            case BlockID.REDSTONE_TORCH:
                switch (meta) {
                    case 1:
                        return 2;
                    case 2:
                        return 1;
                    case 3:
                        return 4;
                    case 4:
                        return 3;
                    // 5 is vertical
                }
                break;

            case BlockID.RAIL:
                switch (meta) {
                    case 6:
                        return 8;
                    case 7:
                        return 9;
                    case 8:
                        return 6;
                    case 9:
                        return 7;
                }
                /* FALL-THROUGH */

            case BlockID.POWERED_RAIL:
            case BlockID.DETECTOR_RAIL:
            case BlockID.ACTIVATOR_RAIL:
                switch (meta & 0x7) {
                    case 0:
                        return 0 | (meta & ~0x7);
                    case 1:
                        return 1 | (meta & ~0x7);
                    case 2:
                        return 3 | (meta & ~0x7);
                    case 3:
                        return 2 | (meta & ~0x7);
                    case 4:
                        return 5 | (meta & ~0x7);
                    case 5:
                        return 4 | (meta & ~0x7);
                }
                break;

            case BlockID.RED_SANDSTONE_STAIRS:
            case BlockID.OAK_WOOD_STAIRS:
            case BlockID.COBBLESTONE_STAIRS:
            case BlockID.BRICK_STAIRS:
            case BlockID.STONE_BRICK_STAIRS:
            case BlockID.NETHER_BRICKS_STAIRS:
            case BlockID.SANDSTONE_STAIRS:
            case BlockID.SPRUCE_WOOD_STAIRS:
            case BlockID.BIRCH_WOOD_STAIRS:
            case BlockID.JUNGLE_WOOD_STAIRS:
            case BlockID.QUARTZ_STAIRS:
            case BlockID.ACACIA_WOODEN_STAIRS:
            case BlockID.DARK_OAK_WOODEN_STAIRS:
            case BlockID.PURPUR_STAIRS:
                switch (meta) {
                    case 0:
                        return 1;
                    case 1:
                        return 0;
                    case 2:
                        return 3;
                    case 3:
                        return 2;
                    case 4:
                        return 5;
                    case 5:
                        return 4;
                    case 6:
                        return 7;
                    case 7:
                        return 6;
                }
                break;

            case BlockID.STONE_BUTTON:
            case BlockID.WOODEN_BUTTON: {
                int thrown = meta & 0x8;
                switch (meta & ~0x8) {
                    case 2:
                        return 3 | thrown;
                    case 3:
                        return 2 | thrown;
                    case 4:
                        return 5 | thrown;
                    case 5:
                        return 4 | thrown;
                    // 0 and 1 are vertical
                }
                break;
            }

            case BlockID.LEVER: {
                int thrown = meta & 0x8;
                switch (meta & ~0x8) {
                    case 1:
                        return 2 | thrown;
                    case 2:
                        return 1 | thrown;
                    case 3:
                        return 4 | thrown;
                    case 4:
                        return 3 | thrown;
                    case 5:
                        return 5 | thrown;
                    case 6:
                        return 6 | thrown;
                    case 7:
                        return 7 | thrown;
                    case 0:
                        return 0 | thrown;
                }
                break;
            }

            case BlockID.WOODEN_DOOR_BLOCK:
            case BlockID.IRON_DOOR_BLOCK:
            case BlockID.SPRUCE_DOOR_BLOCK:
            case BlockID.BIRCH_DOOR_BLOCK:
            case BlockID.JUNGLE_DOOR_BLOCK:
            case BlockID.ACACIA_DOOR_BLOCK:
            case BlockID.DARK_OAK_DOOR_BLOCK:
                if ((meta & 0x8) != 0) {
                    // door top halves contain no orientation information
                    break;
                }

                /* FALL-THROUGH */

            case BlockID.END_PORTAL_FRAME:
            case BlockID.COCOA_BLOCK:
            case BlockID.TRIPWIRE_HOOK: {
                int extra = meta & ~0x3;
                int withoutFlags = meta & 0x3;
                switch (withoutFlags) {
                    case 0:
                        return 2 | extra;
                    case 1:
                        return 3 | extra;
                    case 2:
                        return 0 | extra;
                    case 3:
                        return 1 | extra;
                }
                break;
            }
            case BlockID.SIGN_POST:
            case BlockID.STANDING_BANNER:
                return (meta + 8) % 16;

            case BlockID.LADDER:
            case BlockID.WALL_SIGN:
            case BlockID.WALL_BANNER:
            case BlockID.CHEST:
            case BlockID.FURNACE:
            case BlockID.BURNING_FURNACE:
            case BlockID.ENDER_CHEST:
            case BlockID.TRAPPED_CHEST:
            case BlockID.HOPPER_BLOCK: {
                int extra = meta & 0x8;
                int withoutFlags = meta & ~0x8;
                switch (withoutFlags) {
                    case 2:
                        return 3 | extra;
                    case 3:
                        return 2 | extra;
                    case 4:
                        return 5 | extra;
                    case 5:
                        return 4 | extra;
                }
                break;
            }
            case BlockID.DISPENSER:
            case BlockID.DROPPER:
            case BlockID.END_ROD:
                int dispPower = meta & 0x8;
                switch (meta & ~0x8) {
                    case 2:
                        return 3 | dispPower;
                    case 3:
                        return 2 | dispPower;
                    case 4:
                        return 5 | dispPower;
                    case 5:
                        return 4 | dispPower;
                }
                break;

            case BlockID.PUMPKIN:
            case BlockID.JACK_O_LANTERN:
                switch (meta) {
                    case 0:
                        return 2;
                    case 1:
                        return 3;
                    case 2:
                        return 0;
                    case 3:
                        return 1;
                }
                break;

            case BlockID.HAY_BALE:
            case BlockID.LOG:
            case BlockID.LOG2:
            case BlockID.QUARTZ_BLOCK:
            case BlockID.PURPUR_BLOCK:
            case BlockID.BONE_BLOCK:
                break;

            case BlockID.UNPOWERED_COMPARATOR:
            case BlockID.POWERED_COMPARATOR:
            case BlockID.UNPOWERED_REPEATER:
            case BlockID.POWERED_REPEATER:
                int dir = meta & 0x03;
                int delay = meta - dir;
                switch (dir) {
                    case 0:
                        return 2 | delay;
                    case 1:
                        return 3 | delay;
                    case 2:
                        return 0 | delay;
                    case 3:
                        return 1 | delay;
                }
                break;

            case BlockID.TRAPDOOR:
            case BlockID.IRON_TRAPDOOR:
                int withoutOrientation = meta & ~0x3;
                int orientation = meta & 0x3;
                switch (orientation) {
                    case 0:
                        return 1 | withoutOrientation;
                    case 1:
                        return 0 | withoutOrientation;
                    case 2:
                        return 3 | withoutOrientation;
                    case 3:
                        return 2 | withoutOrientation;
                }
                break;

            case BlockID.PISTON:
            case BlockID.STICKY_PISTON:
            case BlockID.PISTON_HEAD_STICKY:
            case 137: //BlockID.COMMAND_BLOCK
            case 188: //BlockID.REPEATING_COMMAND_BLOCK
            case 189: //BlockID.CHAIN_COMMAND_BLOCK
                final int rest = meta & ~0x7;
                switch (meta & 0x7) {
                    case 2:
                        return 3 | rest;
                    case 3:
                        return 2 | rest;
                    case 4:
                        return 5 | rest;
                    case 5:
                        return 4 | rest;
                }
                break;

            case BlockID.BROWN_MUSHROOM_BLOCK:
            case BlockID.RED_MUSHROOM_BLOCK:
                if (meta >= 10) return meta;
                return (meta * 9) % 10;

            case BlockID.VINE:
                return ((meta << 2) | (meta >> 2)) & 0xf;

            case BlockID.FENCE_GATE:
            case BlockID.FENCE_GATE_SPRUCE:
            case BlockID.FENCE_GATE_BIRCH:
            case BlockID.FENCE_GATE_JUNGLE:
            case BlockID.FENCE_GATE_DARK_OAK:
            case BlockID.FENCE_GATE_ACACIA:
                return ((meta + 2) & 0x3) | (meta & ~0x3);

            case BlockID.ANVIL:
                int damage = meta & ~0x3;
                switch (meta & 0x3) {
                    case 0:
                        return 2 | damage;
                    case 2:
                        return 0 | damage;
                    case 1:
                        return 3 | damage;
                    case 3:
                        return 1 | damage;
                }
                break;

            case BlockID.BED_BLOCK:
                return meta & ~0x3 | (meta + 2) & 0x3;

            case BlockID.SKULL_BLOCK:
            case BlockID.PURPLE_GLAZED_TERRACOTTA:
            case BlockID.WHITE_GLAZED_TERRACOTTA:
            case BlockID.ORANGE_GLAZED_TERRACOTTA:
            case BlockID.MAGENTA_GLAZED_TERRACOTTA:
            case BlockID.LIGHT_BLUE_GLAZED_TERRACOTTA:
            case BlockID.YELLOW_GLAZED_TERRACOTTA:
            case BlockID.LIME_GLAZED_TERRACOTTA:
            case BlockID.PINK_GLAZED_TERRACOTTA:
            case BlockID.GRAY_GLAZED_TERRACOTTA:
            case BlockID.SILVER_GLAZED_TERRACOTTA:
            case BlockID.CYAN_GLAZED_TERRACOTTA:
            case BlockID.BLUE_GLAZED_TERRACOTTA:
            case BlockID.BROWN_GLAZED_TERRACOTTA:
            case BlockID.GREEN_GLAZED_TERRACOTTA:
            case BlockID.RED_GLAZED_TERRACOTTA:
            case BlockID.BLACK_GLAZED_TERRACOTTA:
            case BlockID.OBSERVER:
                switch (meta) {
                    case 2:
                        return 3;
                    case 3:
                        return 2;
                    case 4:
                        return 5;
                    case 5:
                        return 4;
                }
                break;

            case BlockID.NETHER_PORTAL:
                break;

            case BlockID.ITEM_FRAME_BLOCK:
                switch (meta) {
                    case 0:
                        return 1;
                    case 1:
                        return 0;
                    case 2:
                        return 3;
                    case 3:
                        return 2;
                }
                break;

        }

        return meta;
    }
}
