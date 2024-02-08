package cn.nukkit.level.updater.item;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class ItemUpdater_1_20_60 implements Updater {
    public static final Updater INSTANCE = new ItemUpdater_1_20_60();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 20, 60)
                .match("Name", "minecraft:scute")
                .edit("Name", helper -> {
                    helper.getRootTag().put("Name", "minecraft:turtle_scute");
                });
    }
}
