package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.type.BooleanPropertyType;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.LevelException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_DOWN;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_EAST;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_NORTH;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_SOUTH;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_UP;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_WEST;

public class BlockChorusPlant extends BlockTransparent implements StateDeriving {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHORUS_PLANT, CONNECTION_DOWN, CONNECTION_EAST,
        CONNECTION_NORTH, CONNECTION_SOUTH, CONNECTION_UP, CONNECTION_WEST);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChorusPlant() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockChorusPlant(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Chorus Plant";
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 0.4;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    private boolean isPositionValid() {
        // (a chorus plant with at least one other chorus plant horizontally adjacent) breaks unless (at least one of the vertically adjacent blocks is air)
        // (a chorus plant) breaks unless (the block below is (chorus plant or end stone)) or (any horizontally adjacent block is a (chorus plant above (chorus plant or end stone_))
        boolean horizontal = false;
        boolean horizontalSupported = false;
        Block down = down();
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block side = getSide(face);
            if (side.getId().equals(CHORUS_PLANT)) {
                if (!horizontal) {
                    if (!up().getId().equals(AIR) && !down.getId().equals(AIR)) {
                        return false;
                    }
                    horizontal = true;
                }

                Block sideSupport = side.down();
                if (sideSupport.getId().equals(CHORUS_PLANT) || sideSupport.getId().equals(END_STONE)) {
                    horizontalSupported = true;
                }
            }
        }

        if (horizontal && horizontalSupported) {
            return true;
        }
        
        return down.getId().equals(CHORUS_PLANT) || down.getId().equals(END_STONE);
    }

    /**
     * The state a chorus plant takes with the given neighbors, for code that places plants without
     * going through block updates, like the End generator.
     *
     * @param neighbor the block id on each side of the plant
     */
    public static BlockState connectedState(Function<BlockFace, String> neighbor) {
        BlockState state = PROPERTIES.getDefaultState();
        for (BlockFace face : BlockFace.values()) {
            if (connectsTo(neighbor.apply(face), face)) {
                state = state.setPropertyValue(PROPERTIES, connection(face).createValue(true));
            }
        }
        return state;
    }

    // Plants reach into other plants and flowers on every side, and down into the end stone they grow from
    private static boolean connectsTo(String neighborId, BlockFace face) {
        return neighborId.equals(CHORUS_PLANT) || neighborId.equals(CHORUS_FLOWER)
            || face == BlockFace.DOWN && neighborId.equals(END_STONE);
    }

    private static BooleanPropertyType connection(BlockFace face) {
        return switch (face) {
            case DOWN -> CONNECTION_DOWN;
            case UP -> CONNECTION_UP;
            case NORTH -> CONNECTION_NORTH;
            case SOUTH -> CONNECTION_SOUTH;
            case WEST -> CONNECTION_WEST;
            case EAST -> CONNECTION_EAST;
        };
    }

    @Override
    public boolean autoConfigureState() {
        final short previous = this.blockstate.specialValue();
        for (BlockFace face : BlockFace.values()) {
            final boolean connected;
            try {
                connected = connectsTo(this.getSideAtLayer(0, face).getId(), face);
            } catch (LevelException e) {
                // the neighbor is outside a loaded chunk, keep what that side had
                continue;
            }
            this.setPropertyValue(connection(face), connected);
        }
        return this.blockstate.specialValue() != previous;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isPositionValid()) {
                level.scheduleUpdate(this, 1);
                return type;
            }
            if (autoConfigureState()) {
                level.setBlock(this, this, true);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            level.useBreakOn(this, null, null, true);
            return type;
        }
        
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!isPositionValid()) {
            return false;
        }
        autoConfigureState();
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public Item[] getDrops(Item item) {
        return ThreadLocalRandom.current().nextBoolean() ? new Item[]{ Item.get(ItemID.CHORUS_FRUIT, 0, 1) } : Item.EMPTY_ARRAY;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public  boolean sticksToPiston() {
        return false;
    }

}
