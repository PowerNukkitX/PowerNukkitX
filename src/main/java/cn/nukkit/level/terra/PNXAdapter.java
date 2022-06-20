package cn.nukkit.level.terra;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.terra.delegate.PNXBiomeDelegate;
import cn.nukkit.level.terra.delegate.PNXBlockStateDelegate;
import cn.nukkit.level.terra.delegate.PNXItemDelegate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public final class PNXAdapter {
    private static final Map<BlockState, PNXBlockStateDelegate> stateDelegateStore = new WeakHashMap<>(2820, 0.99f);

    @NotNull
    public static PNXItemDelegate adapt(cn.nukkit.item.Item pnxItem) {
        return new PNXItemDelegate(pnxItem);
    }

    public static PNXBiomeDelegate adapt(Biome biome) {
        return new PNXBiomeDelegate(biome);
    }

    public static PNXBlockStateDelegate adapt(BlockState blockState) {
        if (stateDelegateStore.containsKey(blockState)) {
            return stateDelegateStore.get(blockState);
        }
        final var delegate = new PNXBlockStateDelegate(blockState);
        stateDelegateStore.put(blockState, delegate);
        return delegate;
    }
}
