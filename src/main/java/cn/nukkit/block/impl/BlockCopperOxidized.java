package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockCopperOxidized extends BlockCopper {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperOxidized() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Oxidized Copper";
    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @NotNull @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}
