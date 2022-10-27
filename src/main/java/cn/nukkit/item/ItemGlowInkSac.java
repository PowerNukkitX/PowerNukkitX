package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ItemGlowInkSac extends Item {
    public ItemGlowInkSac() {
        this(0, 1);
    }

    public ItemGlowInkSac(Integer meta) {
        this(meta, 1);
    }

    public ItemGlowInkSac(Integer meta, int count) {
        super(GLOW_INK_SAC, 0, count, "Glow Ink Sac");
    }
}
