package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.SimpleAxisAlignedBB;

import static org.powernukkitx.math.VectorMath.calculateFace;

/**
 * @author xtypr
 * @since 2015/12/7
 */
public abstract class BlockFence extends BlockTransparent implements BlockConnectable {
    public BlockFence(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        boolean north = this.canConnect(this.north());
        boolean south = this.canConnect(this.south());
        boolean west = this.canConnect(this.west());
        boolean east = this.canConnect(this.east());
        double n = north ? 0 : 0.375;
        double s = south ? 1 : 0.625;
        double w = west ? 0 : 0.375;
        double e = east ? 1 : 0.625;
        return new SimpleAxisAlignedBB(
            this.x + w,
            this.y,
            this.z + n,
            this.x + e,
            this.y + 1.5,
            this.z + s
        );
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public boolean canConnect(Block block) {
        if (block instanceof BlockFence) {
            if (block.getId().equals(NETHER_BRICK_FENCE) || this.getId().equals(NETHER_BRICK_FENCE)) {
                return block.getId().equals(this.getId());
            }
            return true;
        }
        if (block instanceof BlockTrapdoor trapdoor) {
            return trapdoor.isOpen() && trapdoor.getBlockFace() == calculateFace(this, trapdoor);
        }
        return block instanceof BlockFenceGate || block.isSolid() && !block.isTransparent();
    }


    public boolean autoConfigureState() {
        return HorizontalConnections.configure(this);
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

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        autoConfigureState();
        return super.place(item, block, target, face, fx, fy, fz, player);
    }
}
