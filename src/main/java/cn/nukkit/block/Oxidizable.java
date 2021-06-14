package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;

import javax.annotation.Nonnull;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
@PowerNukkitOnly
@Since("FUTURE")
public interface Oxidizable {
    @PowerNukkitOnly
    @Since("FUTURE")
    @Nonnull
    OxidizationLevel getOxidizationLevel();

    @PowerNukkitOnly
    @Since("FUTURE")
    boolean setOxidizationLevel(@Nonnull OxidizationLevel oxidizationLevel);
}
