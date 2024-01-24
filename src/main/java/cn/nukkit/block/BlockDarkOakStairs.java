package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakStairs  extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Dark Oak Wood Stairs";
    }
}