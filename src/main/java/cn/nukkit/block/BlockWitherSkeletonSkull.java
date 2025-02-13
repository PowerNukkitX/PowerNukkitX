package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.mob.EntityWither;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSkeletonSkull;
import cn.nukkit.item.ItemWitherSkeletonSkull;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockWitherSkeletonSkull extends BlockHead {

    public static final BlockProperties PROPERTIES = new BlockProperties(WITHER_SKELETON_SKULL, CommonBlockProperties.FACING_DIRECTION);

    public BlockWitherSkeletonSkull(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Wither Skeleton Skull";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if(super.place(item, block, target, face, fx, fy, fz, player)) {
            EntityWither.checkAndSpawnWither(this);
            return true;
        }
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemWitherSkeletonSkull();
    }
}
