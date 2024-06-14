package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedDoubleCutCopperSlab extends BlockDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(OXIDIZED_DOUBLE_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockOxidizedCutCopperSlab.PROPERTIES.getDefaultState();
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}