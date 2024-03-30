package cn.nukkit.block;

import cn.nukkit.block.property.enums.FlowerType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

public class BlockYellowFlower extends BlockRedFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(YELLOW_FLOWER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowFlower() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowFlower(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block getUncommonFlower() {
        return get(RED_FLOWER);
    }

    @Override
    public void setFlowerType(FlowerType flowerType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlowerType getFlowerType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }
}