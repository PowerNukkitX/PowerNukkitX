package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockSlabCopperCutOxidized extends BlockSlabCopperCut {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockSlabCopperCutOxidized() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockSlabCopperCutOxidized(int meta) {
        super(meta, OXIDIZED_DOUBLE_CUT_COPPER_SLAB);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    protected BlockSlabCopperCutOxidized(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return OXIDIZED_CUT_COPPER_SLAB;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_NYLIUM_BLOCK_COLOR;
    }
}
