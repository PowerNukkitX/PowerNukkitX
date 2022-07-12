package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ItemGlowInkSac extends StringItem{
    public ItemGlowInkSac() {
        super(MinecraftItemID.GLOW_INK_SAC.getNamespacedId(), "Glow Ink Sac");
    }
}
