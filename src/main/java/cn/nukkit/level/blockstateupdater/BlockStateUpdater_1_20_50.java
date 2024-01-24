package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_20_50 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_20_50();

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
