package cn.nukkit.level.terra;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.terra.delegate.PNXBiomeDelegate;
import cn.nukkit.level.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.terra.delegate.PNXItemDelegate;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public final class PNXAdapter {
    @NotNull
    public static PNXItemDelegate adapt(cn.nukkit.item.Item pnxItem) {
        return new PNXItemDelegate(pnxItem);
    }

    public static PNXBiomeDelegate adapt(Biome biome) {
        return new PNXBiomeDelegate(biome);
    }

    public static PNXBlockStateDelegate adapt(BlockState blockState) {
        return new PNXBlockStateDelegate(blockState);
    }
}
