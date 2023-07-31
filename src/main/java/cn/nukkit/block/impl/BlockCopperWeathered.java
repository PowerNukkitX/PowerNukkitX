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
public class BlockCopperWeathered extends BlockCopper {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockCopperWeathered() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Weathered Copper";
    }

    @Override
    public int getId() {
        return WEATHERED_COPPER;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @NotNull @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}
