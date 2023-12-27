package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMossyStoneBrickStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mossy_stone_brick_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMossyStoneBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMossyStoneBrickStairs(BlockState blockstate) {
        super(blockstate);
    }
}