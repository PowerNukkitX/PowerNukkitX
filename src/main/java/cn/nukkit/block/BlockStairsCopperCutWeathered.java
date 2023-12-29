package cn.nukkit.block;

import cn.nukkit.blockproperty.value.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-06-15
 */


public class BlockStairsCopperCutWeathered extends BlockStairsCopperCut {


    public BlockStairsCopperCutWeathered() {
        this(0);
    }


    public BlockStairsCopperCutWeathered(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return WEATHERED_CUT_COPPER_STAIRS;
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }

}
