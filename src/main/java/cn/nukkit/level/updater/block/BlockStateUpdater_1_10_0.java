package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdater_1_10_0 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_10_0();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        // TODO: mapped types. (I'm not sure if these are needed)
    }
}
