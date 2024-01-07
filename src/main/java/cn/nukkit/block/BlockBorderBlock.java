package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockBorderBlock extends BlockWallBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(BORDER_BLOCK, CommonBlockProperties.WALL_CONNECTION_TYPE_EAST, CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH, CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH, CommonBlockProperties.WALL_CONNECTION_TYPE_WEST, CommonBlockProperties.WALL_POST_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBorderBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBorderBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public String getName() {
        return "Border Block";
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public  boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, BlockFace face, Item item, @Nullable Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }
        return super.isBreakable(vector, layer, face, item, player);
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        AxisAlignedBB aabb = super.recalculateBoundingBox();
        aabb.setMinY(Double.MIN_VALUE);
        aabb.setMaxY(Double.MAX_VALUE);
        return aabb;
    }
}