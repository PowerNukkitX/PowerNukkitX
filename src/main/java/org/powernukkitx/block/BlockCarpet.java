package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/11/24
 */
public abstract class BlockCarpet extends BlockFlowable {
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .hardness(0.1)
            .resistance(0.5)
            .canPassThrough(false)
            .waterloggingLevel(1)
            .build();
    public BlockCarpet(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockCarpet(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.0625;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (!down.isAir()) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL && this.down().isAir()) {
            this.getLevel().useBreakOn(this);

            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }
}
