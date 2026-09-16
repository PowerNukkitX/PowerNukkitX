package org.powernukkitx.level.updater.item;

import org.powernukkitx.level.updater.Updater;
import org.powernukkitx.level.updater.block.BlockStateUpdater_1_26_50;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class ItemUpdater_1_26_50 implements Updater {

    public static final Updater INSTANCE = new ItemUpdater_1_26_50();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 26, 50)
            .match("Name", "minecraft:photo")
            .edit("Name", helper -> helper.getRootTag().put("Name", "minecraft:photo_item"));

        ctx.addUpdater(1, 26, 50, false, false)
            .match("Name", "minecraft:chest_boat")
            .edit("Name", helper -> {
                if (helper.getRootTag().get("Damage") instanceof Number damage && damage.intValue() == 10) {
                    helper.getRootTag().put("Name", "minecraft:poplar_chest_boat");
                    helper.getRootTag().put("Damage", (short) 0);
                }
            });

        for (String identifier : BlockStateUpdater_1_26_50.CONNECTION_BLOCKS) {
            var builder = ctx.addUpdater(1, 26, 50, false, false)
                .match("Name", identifier)
                .visit("Block")
                .visit("states");
            for (String property : BlockStateUpdater_1_26_50.CONNECTION_PROPERTIES) {
                builder.tryAdd(property, (byte) 0);
            }
        }

        for (String identifier : BlockStateUpdater_1_26_50.CORNER_BLOCKS) {
            ctx.addUpdater(1, 26, 50, false, false)
                .match("Name", identifier)
                .visit("Block")
                .visit("states")
                .tryAdd(BlockStateUpdater_1_26_50.CORNER_PROPERTY, BlockStateUpdater_1_26_50.CORNER_DEFAULT);
        }
    }
}
