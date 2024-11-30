package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCreeperHead;
import cn.nukkit.item.ItemPlayerHead;
import org.jetbrains.annotations.NotNull;

public class BlockCreeperHead extends BlockHead {

    public static final BlockProperties PROPERTIES = new BlockProperties(CREEPER_HEAD, CommonBlockProperties.FACING_DIRECTION);

    public BlockCreeperHead(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Creeper Head";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    @Override
    public Item toItem() {
        return new ItemCreeperHead();
    }
}
