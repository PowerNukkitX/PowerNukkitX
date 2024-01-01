package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneStairs extends BlockBlackstoneStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_blackstone_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
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