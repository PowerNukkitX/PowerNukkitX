package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.OptionalBoolean;
import cn.nukkit.utils.Rail;
import cn.nukkit.utils.RedstoneComponent;

import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DATA_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DIRECTION_6;

public class BlockRailPowerable extends BlockRail {
    public static final BlockDefinition DEFINITION = BlockRail.DEFINITION.toBuilder()
            .isPowerSource(true)
            .build();

    public BlockRailPowerable(BlockState blockState) {
        super(blockState, DEFINITION);

        this.canBePowered = true;
    }

    public BlockRailPowerable(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);

        this.canBePowered = true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (super.onUpdate(type) == Level.BLOCK_UPDATE_NORMAL) {
                return 0; // Already broken
            }

            if (!this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
                return 0;
            }
            boolean wasPowered = isActive();
            boolean isPowered = this.isGettingPower()
                    || checkSurrounding(this, true, 0)
                    || checkSurrounding(this, false, 0);

            // Avoid Block mistake
            if (wasPowered != isPowered) {
                setActive(isPowered);
                RedstoneComponent.updateAroundRedstone(down(), BlockFace.UP, BlockFace.DOWN);
                if (getOrientation().isAscending()) {
                    RedstoneComponent.updateAroundRedstone(up(), BlockFace.UP, BlockFace.DOWN);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public void afterRemoval(Block newBlock, boolean update) {
        RedstoneComponent.updateAroundRedstone(down());
        if (getOrientation().isAscending()) {
            RedstoneComponent.updateAroundRedstone(up());
        }
        super.afterRemoval(newBlock, update);
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

        Rail.Orientation base = block.getOrientation();
        boolean onStraight = true;

        switch (base) {
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
        Block block = this.level.getBlock(pos);

        if (!(block instanceof BlockRailPowerable rail)) {
            return false;
        }

        Rail.Orientation base = rail.getOrientation();

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
    public void setRailDirection(Rail.Orientation orientation) {
        this.setPropertyValue(RAIL_DIRECTION_6, orientation.metadata());
    }

    @Override
    public Rail.Orientation getOrientation() {
        return Rail.Orientation.byMetadata(getPropertyValue(RAIL_DIRECTION_6));
    }

    @Override
    public void setActive(boolean active) {
        this.setRailActive(active);
        this.level.setBlock(this, this, true, true);
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
}
