package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockMangrovePropaguleHanging extends BlockMangrovePropagule{
    @Override
    public String getName() {
        return "Mangrove Propaugle Hanging";
    }

    @Override
    public int getId() {
        return MANGROVE_PROPAGULE_HANGING;
    }
}
