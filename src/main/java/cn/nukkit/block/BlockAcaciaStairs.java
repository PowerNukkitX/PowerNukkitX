package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(ACACIA_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Acacia Wood Stairs";
    }
}