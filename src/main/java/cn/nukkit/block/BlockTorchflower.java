package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.FlowerType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


public class BlockTorchflower extends BlockRedFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(TORCHFLOWER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTorchflower() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTorchflower(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        return false;
    }

    public String getName() {
        return "Torchflower";
    }

    @Override
    public void setFlowerType(FlowerType flowerType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlowerType getFlowerType() {
        throw new UnsupportedOperationException();
    }
}