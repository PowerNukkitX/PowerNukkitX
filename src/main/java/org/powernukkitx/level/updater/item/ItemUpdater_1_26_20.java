package org.powernukkitx.level.updater.item;

import org.powernukkitx.level.updater.Updater;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class ItemUpdater_1_26_20 implements Updater {

    public static final Updater INSTANCE = new ItemUpdater_1_26_20();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        this.addMetaUpdater(ctx, "minecraft:bucket", 14, "minecraft:sulfur_cube_bucket");

        this.addMetaUpdater(ctx, "minecraft:spawn_egg", 149, "minecraft:nautilus_spawn_egg");
        this.addMetaUpdater(ctx, "minecraft:spawn_egg", 150, "minecraft:zombie_nautilus_spawn_egg");
        this.addMetaUpdater(ctx, "minecraft:spawn_egg", 151, "minecraft:parched_spawn_egg");
        this.addMetaUpdater(ctx, "minecraft:spawn_egg", 152, "minecraft:camel_husk_spawn_egg");
        this.addMetaUpdater(ctx, "minecraft:spawn_egg", 153, "minecraft:sulfur_cube_spawn_egg");
    }

    private void addMetaUpdater(CompoundTagUpdaterContext ctx, String identifier, int meta, String replacement) {
        ctx.addUpdater(1, 26, 20)
            .match("Name", identifier)
            .edit("Name", helper -> {
                if (helper.getRootTag().get("Damage") instanceof Number damage && damage.intValue() == meta) {
                    helper.getRootTag().put("Name", replacement);
                    helper.getRootTag().put("Damage", (short) 0);
                }
            });
    }
}
