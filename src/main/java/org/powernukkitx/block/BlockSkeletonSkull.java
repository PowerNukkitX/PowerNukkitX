package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemSkeletonSkull;
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
        Item item = new ItemSkeletonSkull();
        item.setBlockUnsafe(this.getProperties().getDefaultState().toBlock());
        return item;
    }
}
