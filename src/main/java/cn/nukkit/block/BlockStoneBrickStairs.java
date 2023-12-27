package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBrickStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stone_brick_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneBrickStairs(BlockState blockstate) {
        super(blockstate);
    }
}