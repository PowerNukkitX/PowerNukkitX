package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockExposedDoubleCutCopperSlab extends BlockDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:exposed_double_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSingleSlabId() {
        return EXPOSED_CUT_COPPER_SLAB;
    }

    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}