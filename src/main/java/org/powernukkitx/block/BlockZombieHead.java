package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemZombieHead;
import org.jetbrains.annotations.NotNull;

public class BlockZombieHead extends BlockHead {

    public static final BlockProperties PROPERTIES = new BlockProperties(ZOMBIE_HEAD, CommonBlockProperties.FACING_DIRECTION);

    public BlockZombieHead(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Zombie Head";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    @Override
    public Item toItem() {
        Item item = new ItemZombieHead();
        item.setBlockUnsafe(this.getProperties().getDefaultState().toBlock());
        return item;
    }
}
