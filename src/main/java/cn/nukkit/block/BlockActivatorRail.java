package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.OptionalBoolean;
import cn.nukkit.utils.Rail;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DATA_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DIRECTION_6;

public class BlockActivatorRail extends BlockRail implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(ACTIVATOR_RAIL, RAIL_DATA_BIT, RAIL_DIRECTION_6);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockActivatorRail() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockActivatorRail(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Activator Rail";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (super.onUpdate(type) == Level.BLOCK_UPDATE_NORMAL) {
                return 0; // Already broken
            }

            if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
                return 0;
            }

            boolean wasPowered = isActive();
            boolean isPowered = this.isGettingPower()
                    || checkSurrounding(this, true, 0)
                    || checkSurrounding(this, false, 0);

            // Avoid Block mistake
            if (wasPowered != isPowered) {
                setActive(isPowered);
                RedstoneComponent.updateAroundRedstone(down());
                if (getOrientation().isAscending()) {
                    RedstoneComponent.updateAroundRedstone(up());
                }
            }
            return type;
        }
        return 0;
    }

    /**
     * Check the surrounding of the rail
     *
     * @param pos      The rail position
     * @param relative The relative of the rail that will be checked
     * @param power    The count of the rail that had been counted
     * @return Boolean of the surrounding area. Where the powered rail on!
     */
    protected boolean checkSurrounding(Vector3 pos, boolean relative, int power) {
        if (power >= 8) {
            return false;
        }

        int dx = pos.getFloorX();
        int dy = pos.getFloorY();
        int dz = pos.getFloorZ();

        BlockRail block;
        Block block2 = level.getBlock(new Vector3(dx, dy, dz));

        if (Rail.isRailBlock(block2)) {
            block = (BlockRail) block2;
        } else {
            return false;
        }

        Rail.Orientation base = null;
        boolean onStraight = true;

        switch (block.getOrientation()) {
            case STRAIGHT_NORTH_SOUTH -> {
                if (relative) {
                    dz++;
                } else {
                    dz--;
                }
            }
            case STRAIGHT_EAST_WEST -> {
                if (relative) {
                    dx--;
                } else {
                    dx++;
                }
            }
            case ASCENDING_EAST -> {
                if (relative) {
                    dx--;
                } else {
                    dx++;
                    dy++;
                    onStraight = false;
                }
                base = Rail.Orientation.STRAIGHT_EAST_WEST;
            }
            case ASCENDING_WEST -> {
                if (relative) {
                    dx--;
                    dy++;
                    onStraight = false;
                } else {
                    dx++;
                }
                base = Rail.Orientation.STRAIGHT_EAST_WEST;
            }
            case ASCENDING_NORTH -> {
                if (relative) {
                    dz++;
                } else {
                    dz--;
                    dy++;
                    onStraight = false;
                }
                base = Rail.Orientation.STRAIGHT_NORTH_SOUTH;
            }
            case ASCENDING_SOUTH -> {
                if (relative) {
                    dz++;
                    dy++;
                    onStraight = false;
                } else {
                    dz--;
                }
                base = Rail.Orientation.STRAIGHT_NORTH_SOUTH;
            }
            default -> {
                return false;
            }
        }

        return canPowered(new Vector3(dx, dy, dz), base, power, relative)
                || onStraight && canPowered(new Vector3(dx, dy - 1., dz), base, power, relative);
    }

    protected boolean canPowered(Vector3 pos, Rail.Orientation state, int power, boolean relative) {
        Block block = level.getBlock(pos);

        if (!(block instanceof BlockActivatorRail)) {
            return false;
        }

        Rail.Orientation base = ((BlockActivatorRail) block).getOrientation();

        return (state != Rail.Orientation.STRAIGHT_EAST_WEST
                || base != Rail.Orientation.STRAIGHT_NORTH_SOUTH
                && base != Rail.Orientation.ASCENDING_NORTH
                && base != Rail.Orientation.ASCENDING_SOUTH)
                && (state != Rail.Orientation.STRAIGHT_NORTH_SOUTH
                || base != Rail.Orientation.STRAIGHT_EAST_WEST
                && base != Rail.Orientation.ASCENDING_EAST
                && base != Rail.Orientation.ASCENDING_WEST)
                && (block.isGettingPower() || checkSurrounding(pos, relative, power + 1));
    }

    @Override
    public boolean isActive() {
        return getPropertyValue(RAIL_DATA_BIT);
    }

    @Override
    public OptionalBoolean isRailActive() {
        return OptionalBoolean.of(getPropertyValue(RAIL_DATA_BIT));
    }

    @Override
    public void setRailActive(boolean active) {
        setPropertyValue(RAIL_DATA_BIT, active);
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    /**
     * Changes the rail direction.
     *
     * @param orientation The new orientation
     */
    public void setRailDirection(Rail.Orientation orientation) {
        setPropertyValue(RAIL_DIRECTION_6, orientation.metadata());
    }

    public Rail.Orientation getOrientation() {
        return Rail.Orientation.byMetadata(getPropertyValue(RAIL_DIRECTION_6));
    }
}