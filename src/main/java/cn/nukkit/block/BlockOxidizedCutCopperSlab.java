package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCutCopperSlab extends BlockCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oxidized_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }

    public BlockOxidizedCutCopperSlab(BlockState blockstate,String doubleSlabId) {
        super(blockstate, doubleSlabId);
    }

    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}