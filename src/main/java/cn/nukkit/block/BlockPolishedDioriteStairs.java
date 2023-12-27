package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDioriteStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_diorite_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDioriteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDioriteStairs(BlockState blockstate) {
        super(blockstate);
    }
}