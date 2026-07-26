package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemDragonHead;
import org.jetbrains.annotations.NotNull;

public class BlockDragonHead extends BlockHead {

    public static final BlockProperties PROPERTIES = new BlockProperties(DRAGON_HEAD, CommonBlockProperties.FACING_DIRECTION);

    public BlockDragonHead(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Dragon Head";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    @Override
    public Item toItem() {
        Item item = new ItemDragonHead();
        item.setBlockUnsafe(this.getProperties().getDefaultState().toBlock());
        return item;
    }
}
