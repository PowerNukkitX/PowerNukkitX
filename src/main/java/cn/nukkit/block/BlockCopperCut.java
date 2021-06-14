package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockCopperCut extends BlockCopperBase {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperCut() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Cut Copper";
    }

    @Override
    public int getId() {
        return CUT_COPPER;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
