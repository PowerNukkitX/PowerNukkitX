package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDragonHead;
import cn.nukkit.item.ItemSkeletonSkull;
import org.jetbrains.annotations.NotNull;

public class BlockSkeletonSkull extends BlockHead {

    public static final BlockProperties PROPERTIES = new BlockProperties(SKELETON_SKULL, CommonBlockProperties.FACING_DIRECTION);

    public BlockSkeletonSkull(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Skeleton Skull";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    @Override
    public Item toItem() {
        return new ItemSkeletonSkull();
    }
}
