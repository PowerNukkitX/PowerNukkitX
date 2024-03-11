package cn.nukkit.level.generator.terra;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.terra.delegate.PNXBiomeDelegate;
import cn.nukkit.level.generator.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.generator.terra.delegate.PNXItemDelegate;
import org.jetbrains.annotations.NotNull;

public final class PNXAdapter {
    @NotNull
    public static PNXItemDelegate adapt(cn.nukkit.item.Item pnxItem) {
        return new PNXItemDelegate(pnxItem);
    }

    public static PNXBiomeDelegate adapt(int biome) {
        return new PNXBiomeDelegate(biome);
    }

    public static PNXBlockStateDelegate adapt(BlockState blockState) {
        return new PNXBlockStateDelegate(blockState);
    }
}
