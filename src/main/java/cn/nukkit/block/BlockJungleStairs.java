package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Jungle Wood Stairs";
    }
}