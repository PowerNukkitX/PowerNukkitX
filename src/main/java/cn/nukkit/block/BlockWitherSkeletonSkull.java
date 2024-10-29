package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSkeletonSkull;
import cn.nukkit.item.ItemWitherSkeletonSkull;
import org.jetbrains.annotations.NotNull;

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
    public Item toItem() {
        return new ItemWitherSkeletonSkull();
    }
}
