package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOakStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(OAK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Oak Wood Stairs";
    }
}