package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedAndesiteStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_andesite_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedAndesiteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedAndesiteStairs(BlockState blockstate) {
        super(blockstate);
    }
}