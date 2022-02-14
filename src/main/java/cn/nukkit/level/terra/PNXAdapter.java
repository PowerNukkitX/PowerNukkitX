package cn.nukkit.level.terra;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.terra.delegate.PNXItemDelegate;
import org.jetbrains.annotations.NotNull;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public final class PNXAdapter {
    @NotNull
    public static PNXItemDelegate adapt(cn.nukkit.item.Item pnxItem) {
        return new PNXItemDelegate(pnxItem);
    }
}
