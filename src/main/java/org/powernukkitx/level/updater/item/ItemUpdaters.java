package org.powernukkitx.level.updater.item;

import org.powernukkitx.level.updater.Updater;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;
import org.powernukkitx.nbt.tag.CompoundTag;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemUpdaters {

    private static final CompoundTagUpdaterContext CONTEXT;
    private static final int LATEST_VERSION;

    static {
        List<Updater> updaters = new ArrayList<>();
        updaters.add(ItemUpdater_1_20_60.INSTANCE);
        updaters.add(ItemUpdater_1_20_70.INSTANCE);
        updaters.add(ItemUpdater_1_20_80.INSTANCE);
        updaters.add(ItemUpdater_1_21_0.INSTANCE);
        updaters.add(ItemUpdater_1_21_20.INSTANCE);
        updaters.add(ItemUpdater_1_21_30.INSTANCE);
        updaters.add(ItemUpdater_1_21_40.INSTANCE);
        updaters.add(ItemUpdater_1_21_110.INSTANCE);

        CompoundTagUpdaterContext context = new CompoundTagUpdaterContext();
        updaters.forEach(updater -> updater.registerUpdaters(context));
        CONTEXT = context;
        LATEST_VERSION = context.getLatestVersion();
    }

    public static CompoundTag updateItem(CompoundTag tag, int version) {
        return CompoundTag.fromNetwork(CONTEXT.update(tag.toNetwork(), version));
    }

    public static int getLatestVersion() {
        return LATEST_VERSION;
    }
}
