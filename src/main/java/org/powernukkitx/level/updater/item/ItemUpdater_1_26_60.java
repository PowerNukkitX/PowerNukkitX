package org.powernukkitx.level.updater.item;

import org.powernukkitx.level.updater.Updater;
import org.powernukkitx.level.updater.block.BlockStateUpdater_1_26_60;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class ItemUpdater_1_26_60 implements Updater {

    public static final Updater INSTANCE = new ItemUpdater_1_26_60();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        BlockStateUpdater_1_26_60.ADDED_PROPERTIES.forEach((identifier, properties) -> {
            var builder = ctx.addUpdater(1, 26, 60, false, false)
                .match("Name", identifier)
                .visit("Block")
                .visit("states");
            properties.forEach(builder::tryAdd);
        });
    }
}
