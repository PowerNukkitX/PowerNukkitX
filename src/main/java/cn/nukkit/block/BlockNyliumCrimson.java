package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockNyliumCrimson extends BlockNylium {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockNyliumCrimson() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Crimson Nylium";
    }

    @Override
    public int getId() {
        return CRIMSON_NYLIUM;
    }

}
