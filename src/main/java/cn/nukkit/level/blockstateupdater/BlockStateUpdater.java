package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;

public interface BlockStateUpdater {

    void registerUpdaters(CompoundTagUpdaterContext context);

}
