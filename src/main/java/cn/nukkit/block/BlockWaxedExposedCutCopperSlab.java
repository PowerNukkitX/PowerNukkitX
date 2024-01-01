package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCutCopperSlab extends BlockExposedCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_exposed_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCutCopperSlab(BlockState blockstate) {
        super(blockstate, WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB);
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}