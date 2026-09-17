package org.powernukkitx.level.updater.block;

import org.powernukkitx.level.updater.Updater;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_26_30 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_26_30();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 26, 30)
            .match("name", "minecraft:potent_sulfur")
            .visit("states")
            .tryAdd("potent_sulfur_state", "dry");
    }
}
