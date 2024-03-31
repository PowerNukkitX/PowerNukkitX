package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCutCopperSlab extends BlockCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(OXIDIZED_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCutCopperSlab(BlockState blockstate) {
        super(blockstate, OXIDIZED_DOUBLE_CUT_COPPER_SLAB);
    }

    protected BlockOxidizedCutCopperSlab(BlockState blockstate, String doubleSlabId) {
        super(blockstate, doubleSlabId);
    }

    @Override
    @NotNull
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}