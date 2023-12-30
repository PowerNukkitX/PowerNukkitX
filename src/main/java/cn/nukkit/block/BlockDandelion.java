package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.value.SmallFlowerType;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockDandelion extends BlockRedFlower {
    public BlockDandelion() {
        this(0);
    }

    public BlockDandelion(int meta) {
        super(0);
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public int getId() {
        return DANDELION;
    }

    @Override
    protected Block getUncommonFlower() {
        return get(RED_FLOWER);
    }


    @Override
    public void setFlowerType(SmallFlowerType flowerType) {
        setOnSingleFlowerType(SmallFlowerType.DANDELION, flowerType);
    }


    @Override
    public SmallFlowerType getFlowerType() {
        return SmallFlowerType.DANDELION;
    }
}
