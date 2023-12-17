package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */


public class BlockCopperCutOxidized extends BlockCopperCut {


    public BlockCopperCutOxidized() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Cut Oxidized Copper";
    }

    @Override
    public int getId() {
        return OXIDIZED_CUT_COPPER;
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}
