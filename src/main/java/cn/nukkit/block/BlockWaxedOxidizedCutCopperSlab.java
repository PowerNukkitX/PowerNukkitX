package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedCutCopperSlab extends BlockOxidizedCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_oxidized_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedCutCopperSlab(BlockState blockstate) {
        super(blockstate, WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB);
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}