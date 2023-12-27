package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrickStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brick_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrickStairs(BlockState blockstate) {
        super(blockstate);
    }
}