package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCutCopperSlab extends BlockCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:weathered_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCutCopperSlab(BlockState blockstate) {
        super(blockstate, WEATHERED_DOUBLE_CUT_COPPER_SLAB);
    }

    protected BlockWeatheredCutCopperSlab(BlockState blockstate, String doubleSlab) {
        super(blockstate, doubleSlab);
    }

    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}