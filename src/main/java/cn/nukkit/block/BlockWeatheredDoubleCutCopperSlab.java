package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredDoubleCutCopperSlab extends BlockDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:weathered_double_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getSingleSlabId() {
        return WEATHERED_CUT_COPPER_SLAB;
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}