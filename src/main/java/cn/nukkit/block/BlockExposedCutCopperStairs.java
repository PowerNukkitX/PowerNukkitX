package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCutCopperStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:exposed_cut_copper_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCutCopperStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCutCopperStairs(BlockState blockstate) {
        super(blockstate);
    }
}