package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockNetherBrickStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:nether_brick_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherBrickStairs(BlockState blockstate) {
        super(blockstate);
    }
}