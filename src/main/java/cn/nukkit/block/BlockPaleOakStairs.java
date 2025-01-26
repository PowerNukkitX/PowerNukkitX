package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPaleOakStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleOakStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleOakStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Pale Oak Wood Stairs";
    }
}