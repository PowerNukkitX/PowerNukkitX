package org.powernukkitx.level.generator.terra;

import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.generator.terra.delegate.PNXBiomeDelegate;
import org.powernukkitx.level.generator.terra.delegate.PNXBlockStateDelegate;
import org.powernukkitx.level.generator.terra.delegate.PNXItemDelegate;
import org.jetbrains.annotations.NotNull;

public final class PNXAdapter {
    @NotNull
    public static PNXItemDelegate adapt(org.powernukkitx.item.Item pnxItem) {
        return new PNXItemDelegate(pnxItem);
    }

    public static PNXBiomeDelegate adapt(int biome) {
        return new PNXBiomeDelegate(biome);
    }

    public static PNXBlockStateDelegate adapt(BlockState blockState) {
        return new PNXBlockStateDelegate(blockState);
    }
}
