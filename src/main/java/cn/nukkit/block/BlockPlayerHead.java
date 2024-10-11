package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPlayerHead;
import org.jetbrains.annotations.NotNull;

public class BlockPlayerHead extends BlockHead {

    public static final BlockProperties PROPERTIES = new BlockProperties(PLAYER_HEAD, CommonBlockProperties.FACING_DIRECTION);

    public BlockPlayerHead(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Player Head";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    @Override
    public Item toItem() {
        return new ItemPlayerHead();
    }
}
