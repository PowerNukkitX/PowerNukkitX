package cn.nukkit.block.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.value.SmallFlowerType;
import org.jetbrains.annotations.NotNull;

public class BlockTorchflower extends BlockFlower {
    public BlockTorchflower() {
        this(0);
    }

    public BlockTorchflower(int meta) {
        super(0);
    }

    public int getId() {
        return TORCHFLOWER;
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    public String getName() {
        return "Torchflower";
    }

    @Override
    protected Block getUncommonFlower() {
        return get(RED_FLOWER);
    }

    @Override
    public void setFlowerType(SmallFlowerType flowerType) {
        setOnSingleFlowerType(SmallFlowerType.TORCHFLOWER, flowerType);
    }

    @Override
    public SmallFlowerType getFlowerType() {
        return SmallFlowerType.TORCHFLOWER;
    }
}
