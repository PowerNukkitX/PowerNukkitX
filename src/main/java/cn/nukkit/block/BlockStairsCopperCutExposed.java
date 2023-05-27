package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;

import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-06-15
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockStairsCopperCutExposed extends BlockStairsCopperCut {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockStairsCopperCutExposed() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockStairsCopperCutExposed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return EXPOSED_CUT_COPPER_STAIRS;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }

}
