package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/1
 */
public class BlockWaterlily extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(WATERLILY);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaterlily() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaterlily(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Lily Pad";
    }

    @Override
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.015625;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target instanceof BlockFlowingWater || target.getLevelBlockAtLayer(1) instanceof BlockFlowingWater) {
            Block up = target.up();
            if (up.isAir()) {
                this.getLevel().setBlock(up, this, true, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = this.down();
            if (!(down instanceof BlockFlowingWater) && !(down.getLevelBlockAtLayer(1) instanceof BlockFlowingWater)
                    && !(down instanceof BlockFrostedIce) && !(down.getLevelBlockAtLayer(1) instanceof BlockFrostedIce)) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }
}
