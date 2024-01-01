package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedDoubleCutCopperSlab extends BlockDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oxidized_double_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getSingleSlabId() {
        return OXIDIZED_CUT_COPPER_SLAB;
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}