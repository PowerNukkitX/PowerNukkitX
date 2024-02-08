package cn.nukkit.level.updater;

import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public interface Updater {
    void registerUpdaters(CompoundTagUpdaterContext context);
}
