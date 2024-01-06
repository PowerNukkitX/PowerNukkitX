package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneBrickStairs extends BlockPolishedBlackstoneStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_BRICK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneBrickStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Polished Blackstone Brick Stairs";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }
}