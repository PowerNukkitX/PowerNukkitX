package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDragonHead;
import cn.nukkit.item.ItemHead;
import cn.nukkit.item.ItemPiglinHead;
import org.jetbrains.annotations.NotNull;

public class BlockPiglinHead extends BlockHead implements ItemHead {

    public static final BlockProperties PROPERTIES = new BlockProperties(PIGLIN_HEAD, CommonBlockProperties.FACING_DIRECTION);

    public BlockPiglinHead(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Piglin Head";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    @Override
    public Item toItem() {
        return new ItemPiglinHead();
    }
}
