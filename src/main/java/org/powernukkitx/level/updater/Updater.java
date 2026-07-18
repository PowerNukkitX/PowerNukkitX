package org.powernukkitx.level.updater;

import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public interface Updater {
    void registerUpdaters(CompoundTagUpdaterContext context);
}
