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
public class BlockSlabCopperCutExposed extends BlockSlabCopperCut {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockSlabCopperCutExposed() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockSlabCopperCutExposed(int meta) {
        super(meta, EXPOSED_DOUBLE_CUT_COPPER_SLAB);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    protected BlockSlabCopperCutExposed(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return EXPOSED_CUT_COPPER_SLAB;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }
}
