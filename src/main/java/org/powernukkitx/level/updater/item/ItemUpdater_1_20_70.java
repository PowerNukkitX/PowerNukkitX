package org.powernukkitx.level.updater.item;

import org.powernukkitx.level.updater.Updater;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class ItemUpdater_1_20_70 implements Updater {
    public static final Updater INSTANCE = new ItemUpdater_1_20_70();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 20, 70)
                .match("Name", "minecraft:grass")
                .edit("Name", helper -> {
                    helper.getRootTag().put("Name", "minecraft:grass_block");
                });
    }
}
