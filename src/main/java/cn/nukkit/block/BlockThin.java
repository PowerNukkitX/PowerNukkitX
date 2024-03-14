package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.LevelException;

import static cn.nukkit.math.VectorMath.calculateFace;

/**
 * @author xtypr
 * @since 2015/12/6
 * @apiNote Implements BlockConnectable only in PowerNukkit
 */

public abstract class BlockThin extends BlockTransparent implements BlockConnectable {

    public BlockThin(BlockState blockState) {
        super(blockState);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        final double offNW = 7.0 / 16.0;
        final double offSE = 9.0 / 16.0;
        final double onNW = 0.0;
        final double onSE = 1.0;
        double w = offNW;
        double e = offSE;
        double n = offNW;
        double s = offSE;
        try {
            boolean north = this.canConnect(this.north());
            boolean south = this.canConnect(this.south());
            boolean west = this.canConnect(this.west());
            boolean east = this.canConnect(this.east());
            w = west ? onNW : offNW;
            e = east ? onSE : offSE;
            n = north ? onNW : offNW;
            s = south ? onSE : offSE;
        } catch (LevelException ignore) {
            //null sucks
        }
        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1,
                this.z + s
        );
    }

    @Override
    public boolean canConnect(Block block) {
        return switch (block.getId()) {
            case GLASS_PANE, BLACK_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, BROWN_STAINED_GLASS_PANE,
                    CYAN_STAINED_GLASS_PANE, GRAY_STAINED_GLASS_PANE, GREEN_STAINED_GLASS_PANE,
                    LIGHT_BLUE_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS_PANE, LIME_STAINED_GLASS_PANE,
                    MAGENTA_STAINED_GLASS_PANE, ORANGE_STAINED_GLASS_PANE, PINK_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS_PANE,
                    RED_STAINED_GLASS_PANE, WHITE_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS_PANE, IRON_BARS, COBBLESTONE_WALL, COBBLED_DEEPSLATE_WALL ->
                    true;
            default -> {
                if (block instanceof BlockTrapdoor trapdoor) {
                    yield trapdoor.isOpen() && trapdoor.getBlockFace() == calculateFace(this, trapdoor);
                }
                yield block.isSolid();
            }
        };
    }
}
