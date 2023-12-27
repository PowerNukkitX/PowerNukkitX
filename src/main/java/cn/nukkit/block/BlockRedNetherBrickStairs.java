package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedNetherBrickStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_nether_brick_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedNetherBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedNetherBrickStairs(BlockState blockstate) {
        super(blockstate);
    }
}