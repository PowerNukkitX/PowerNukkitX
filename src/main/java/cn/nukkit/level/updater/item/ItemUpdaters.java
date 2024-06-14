package cn.nukkit.level.updater.item;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;
import cn.nukkit.nbt.tag.CompoundTag;
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

        CompoundTagUpdaterContext context = new CompoundTagUpdaterContext();
        updaters.forEach(updater -> updater.registerUpdaters(context));
        CONTEXT = context;
        LATEST_VERSION = context.getLatestVersion();
    }

    public static CompoundTag updateItem(CompoundTag tag, int version) {
        return CONTEXT.update(tag, version);
    }

    public static int getLatestVersion() {
        return LATEST_VERSION;
    }
}
