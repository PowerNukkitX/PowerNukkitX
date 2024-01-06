package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneStairs extends BlockBlackstoneStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Polished Blackstone Stairs";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }
}