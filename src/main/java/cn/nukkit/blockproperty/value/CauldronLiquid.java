package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.5.0.0-PN")
@PowerNukkitXDifference(since = "1.20.0-r2", info = "WATER is the last one now")
public enum CauldronLiquid {
    @Since("1.5.0.0-PN") @PowerNukkitOnly LAVA,
    @Since("1.5.0.0-PN") @PowerNukkitOnly POWDER_SNOW,
    @Since("1.5.0.0-PN") @PowerNukkitOnly WATER,
}
