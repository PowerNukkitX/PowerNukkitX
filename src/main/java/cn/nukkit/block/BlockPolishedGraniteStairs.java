package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedGraniteStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_granite_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedGraniteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedGraniteStairs(BlockState blockstate) {
        super(blockstate);
    }
}