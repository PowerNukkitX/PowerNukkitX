package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.OptionalBoolean;
import cn.nukkit.utils.Rail;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DATA_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DIRECTION_6;

public class BlockGoldenRail extends BlockRail implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(GOLDEN_RAIL, RAIL_DATA_BIT, RAIL_DIRECTION_6);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGoldenRail() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGoldenRail(BlockState blockstate) {
        super(blockstate);
        canBePowered = true;
    }

    @Override
    public String getName() {
        return "Powered Rail";
    }

    @Override
    public int onUpdate(int type) {
        // Warning: I didn't recommend this on slow networks server or slow client
        //          Network below 86Kb/s. This will became unresponsive to clients
        //          When updating the block state. Espicially on the world with many rails.
        //          Trust me, I tested this on my server.
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
    private boolean checkSurrounding(Vector3 pos, boolean relative, int power) {
        // The powered rail can power up to 8 blocks only
        if (power >= 8) {
            return false;
        }
        // The position of the floor numbers
        int dx = pos.getFloorX();
        int dy = pos.getFloorY();
        int dz = pos.getFloorZ();
        // First: get the base block
        BlockRail block;
        Block block2 = level.getBlock(new Vector3(dx, dy, dz));

        // Second: check if the rail is Powered rail
        if (Rail.isRailBlock(block2)) {
            block = (BlockRail) block2;
        } else {
            return false;
        }

        // Used to check if the next ascending rail should be what
        Rail.Orientation base = block.getOrientation();
        boolean onStraight = true;
        // Third: Recalculate the base position
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
            }
            case ASCENDING_WEST -> {
                if (relative) {
                    dx--;
                    dy++;
                    onStraight = false;
                } else {
                    dx++;
                }
            }
            case ASCENDING_NORTH -> {
                if (relative) {
                    dz++;
                } else {
                    dz--;
                    dy++;
                    onStraight = false;
                }
            }
            case ASCENDING_SOUTH -> {
                if (relative) {
                    dz++;
                    dy++;
                    onStraight = false;
                } else {
                    dz--;
                }
            }
            default -> {
                // Unable to determinate the rail orientation
                // Wrong rail?
                return false;
            }
        }
        // Next check the if rail is on power state
        return canPowered(new Vector3(dx, dy, dz), base, power, relative)
                || onStraight && canPowered(new Vector3(dx, dy - 1.0D, dz), base, power, relative);
    }

    protected boolean canPowered(Vector3 pos, Rail.Orientation state, int power, boolean relative) {
        Block block = level.getBlock(pos);
        // What! My block is air??!! Impossible! XD
        if (!(block instanceof BlockGoldenRail)) {
            return false;
        }

        // Sometimes the rails are diffrent orientation
        Rail.Orientation base = ((BlockGoldenRail) block).getOrientation();

        // Possible way how to know when the rail is activated is rail were directly powered
        // OR recheck the surrounding... Which will returns here =w=
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
        setPropertyValue(RAIL_DIRECTION_6, orientation.metadata());
    }

    public Rail.Orientation getOrientation() {
        return Rail.Orientation.byMetadata(getPropertyValue(RAIL_DIRECTION_6));
    }

    @Override
    public void setActive(boolean active) {
        setRailActive(active);
        level.setBlock(this, this, true, true);
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