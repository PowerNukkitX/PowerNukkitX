package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for cactus flower block.
 *
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/7/15
 */
public class BlockCactusFlower extends BlockFlower {

    public static final BlockProperties PROPERTIES = new BlockProperties(CACTUS_FLOWER);

    public BlockCactusFlower() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCactusFlower(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Cactus Flower";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!isBlockValidSupport(block.down())) {
            return false;
        }
        return this.getLevel().setBlock(this, this);
    }

    private boolean isBlockValidSupport(Block block) {
        Vector3 check = new Vector3(this.getX() +.5f, this.getY(), this.getZ() + .5f);
        AxisAlignedBB box = block.recalculateBoundingBox();
        return (!block.isAir() && box != null && box.isVectorInside(check)) || block instanceof BlockCactus;
    }

    @Override
    public boolean canPlantOn(Block block) {
        return isBlockValidSupport(block);
    }
}
