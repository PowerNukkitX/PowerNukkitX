package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDragonHead;
import cn.nukkit.item.ItemZombieHead;
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
        return new ItemDragonHead();
    }
}
