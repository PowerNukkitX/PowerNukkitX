package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedFlower extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_flower", CommonBlockProperties.FLOWER_TYPE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedFlower() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedFlower(BlockState blockstate) {
        super(blockstate);
    }
}