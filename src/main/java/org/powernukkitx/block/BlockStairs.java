package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.utils.Faceable;
import org.powernukkitx.block.definition.BlockDefinition;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.UPSIDE_DOWN_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.WEIRDO_DIRECTION;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockStairs extends BlockTransparent implements Faceable {
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .waterloggingLevel(1)
            .build();

    public BlockStairs(BlockState blockState) {
        this(blockState, DEFINITION);
    }

    public BlockStairs(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
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
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public boolean collidesWithBB(AxisAlignedBB bb) {
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

        if (bb.intersectsWith(new SimpleAxisAlignedBB(
                this.x,
                this.y + minSlabY,
                this.z,
                this.x + 1,
                this.y + maxSlabY,
                this.z + 1
        ))) {
            return true;
        }

        return switch (face) {
            case EAST -> bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.x + 0.5,
                    this.y + minHalfSlabY,
                    this.z,
                    this.x + 1,
                    this.y + maxHalfSlabY,
                    this.z + 1
            ));
            case WEST -> bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.x,
                    this.y + minHalfSlabY,
                    this.z,
                    this.x + 0.5,
                    this.y + maxHalfSlabY,
                    this.z + 1
            ));
            case SOUTH -> bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.x,
                    this.y + minHalfSlabY,
                    this.z + 0.5,
                    this.x + 1,
                    this.y + maxHalfSlabY,
                    this.z + 1
            ));
            case NORTH -> bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.x,
                    this.y + minHalfSlabY,
                    this.z,
                    this.x + 1,
                    this.y + maxHalfSlabY,
                    this.z + 0.5
            ));
            default -> false;
        };
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
