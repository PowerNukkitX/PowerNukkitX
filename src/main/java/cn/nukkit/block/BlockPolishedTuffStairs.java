package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedTuffStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_tuff_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedTuffStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedTuffStairs(BlockState blockstate) {
        super(blockstate);
    }
}