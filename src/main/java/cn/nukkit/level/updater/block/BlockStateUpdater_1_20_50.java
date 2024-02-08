package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_20_50 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_20_50();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 20, 50)
                .match("name", "minecraft:planks")
                .visit("states")
                .edit("wood_type", helper -> {
                    String type = (String) helper.getTag();
                    helper.getRootTag().put("name", "minecraft:" + type + "_planks");
                })
                .remove("wood_type");

        ctx.addUpdater(1, 20, 50)
                .match("name", "minecraft:stone")
                .visit("states")
                .edit("stone_type", helper -> {
                    String type = (String) helper.getTag();
                    switch (type) {
                        case "andesite_smooth":
                            type = "polished_andesite";
                            break;
                        case "diorite_smooth":
                            type = "polished_diorite";
                            break;
                        case "granite_smooth":
                            type = "polished_granite";
                            break;
                    }
                    helper.getRootTag().put("name", "minecraft:" + type);
                })
                .remove("stone_type");

    }
}
