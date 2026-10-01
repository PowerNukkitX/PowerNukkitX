package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.block.property.enums.Corner;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.utils.LevelException;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.CORNER;
import static org.powernukkitx.block.property.CommonBlockProperties.UPSIDE_DOWN_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.WEIRDO_DIRECTION;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockStairs extends BlockTransparent implements Faceable {
    public BlockStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getMinY() {
        return this.y + (isUpsideDown() ? 0.5 : 0);
    }

    @Override
    public double getMaxY() {
        return this.y + (isUpsideDown() ? 1 : 0.5);
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return side == BlockFace.UP && isUpsideDown() || side == BlockFace.DOWN && !isUpsideDown();
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            setBlockFace(player.getDirection());
        }

        if ((fy > 0.5 && face != BlockFace.UP) || face == BlockFace.DOWN) {
            setUpsideDown(true);
        }
        autoConfigureState();
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }
    /**
     * Automatically configures the corner state.
     * @return whether the shape changed
     */
    public boolean autoConfigureState() {
        final short previous = blockstate.specialValue();
        setPropertyValue(CORNER, computeCorner());
        return blockstate.specialValue() != previous; // wut
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (autoConfigureState()) {
                level.setBlock(this, this, true);
            }
            return type;
        }
        return super.onUpdate(type);
    }

    private Corner computeCorner() {
        final BlockFace facing = getBlockFace();

        final BlockFace ahead = adjoiningStairsFacing(facing);
        if (ahead != null && canTakeShape(ahead.getOpposite())) {
            return ahead == facing.rotateYCCW() ? Corner.OUTER_LEFT : Corner.OUTER_RIGHT;
        }

        final BlockFace behind = adjoiningStairsFacing(facing.getOpposite());
        if (behind != null && canTakeShape(behind)) {
            return behind == facing.rotateYCCW() ? Corner.INNER_LEFT : Corner.INNER_RIGHT;
        }

        return Corner.NONE;
    }

    /**
     * The stairs on the given side, or {@code null} when there are none there and when the block
     * cannot be read at all. A neighbor in a chunk that is not loaded leaves the shape as it is
     * rather than guessing at it.
     */
    private BlockStairs neighbourStairs(BlockFace side) {
        try {
            return getSideAtLayer(0, side) instanceof BlockStairs neighbour ? neighbour : null;
        } catch (LevelException e) {
            return null;
        }
    }

    /**
     * The direction of the stairs on the given side when they can shape this block - that is, when
     * they sit in the same half and run across it rather than along it.
     */
    private BlockFace adjoiningStairsFacing(BlockFace side) {
        final BlockStairs neighbour = neighbourStairs(side);
        if (neighbour == null || neighbour.isUpsideDown() != isUpsideDown()) {
            return null;
        }
        final BlockFace neighbourFacing = neighbour.getBlockFace();
        return neighbourFacing.getAxis() == getBlockFace().getAxis() ? null : neighbourFacing;
    }

    private boolean canTakeShape(BlockFace side) {
        final BlockStairs neighbour = neighbourStairs(side);
        return neighbour == null
            || neighbour.getBlockFace() != getBlockFace()
            || neighbour.isUpsideDown() != isUpsideDown();
    }

    @Override
    public boolean collidesWithBB(AxisAlignedBB bb) {
        for (AxisAlignedBB collisionBox : getCollisionBoxes()) {
            if (bb.intersectsWith(collisionBox)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AxisAlignedBB[] getCollisionBoxes() {
        BlockFace face = getBlockFace();
        double minSlabY = 0;
        double maxSlabY = 0.5;
        double minHalfSlabY = 0.5;
        double maxHalfSlabY = 1;

        if (isUpsideDown()) {
            minSlabY = 0.5;
            maxSlabY = 1;
            minHalfSlabY = 0;
            maxHalfSlabY = 0.5;
        }

        AxisAlignedBB slab = new SimpleAxisAlignedBB(
            this.x,
            this.y + minSlabY,
            this.z,
            this.x + 1,
            this.y + maxSlabY,
            this.z + 1
        );
        AxisAlignedBB step = switch (face) {
            case EAST -> new SimpleAxisAlignedBB(
                this.x + 0.5,
                this.y + minHalfSlabY,
                this.z,
                this.x + 1,
                this.y + maxHalfSlabY,
                this.z + 1
            );
            case WEST -> new SimpleAxisAlignedBB(
                this.x,
                this.y + minHalfSlabY,
                this.z,
                this.x + 0.5,
                this.y + maxHalfSlabY,
                this.z + 1
            );
            case SOUTH -> new SimpleAxisAlignedBB(
                this.x,
                this.y + minHalfSlabY,
                this.z + 0.5,
                this.x + 1,
                this.y + maxHalfSlabY,
                this.z + 1
            );
            case NORTH -> new SimpleAxisAlignedBB(
                this.x,
                this.y + minHalfSlabY,
                this.z,
                this.x + 1,
                this.y + maxHalfSlabY,
                this.z + 0.5
            );
            default -> null;
        };
        return step == null ? new AxisAlignedBB[]{slab} : new AxisAlignedBB[]{slab, step};
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    public void setUpsideDown(boolean upsideDown) {
        setPropertyValue(UPSIDE_DOWN_BIT, upsideDown);
    }

    public boolean isUpsideDown() {
        return getPropertyValue(UPSIDE_DOWN_BIT);
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.EWSN_DIRECTION.inverse().get(getPropertyValue(WEIRDO_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(WEIRDO_DIRECTION, CommonPropertyMap.EWSN_DIRECTION.get(face));
    }
}
