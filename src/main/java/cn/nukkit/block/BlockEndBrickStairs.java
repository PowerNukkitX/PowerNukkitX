package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockEndBrickStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:end_brick_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEndBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEndBrickStairs(BlockState blockstate) {
        super(blockstate);
    }
}