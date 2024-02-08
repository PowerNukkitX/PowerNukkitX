package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_20_60 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_20_60();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 20, 60)
                .match("name", "minecraft:hard_stained_glass")
                .visit("states")
                .edit("color", helper -> {
                    String color = (String) helper.getTag();
                    if (color.equals("silver")) {
                        color = "light_gray";
                    }
                    helper.getRootTag().put("name", "minecraft:hard_" + color + "_stained_glass");
                })
                .remove("color");

        ctx.addUpdater(1, 20, 60)
                .match("name", "minecraft:hard_stained_glass_pane")
                .visit("states")
                .edit("color", helper -> {
                    String color = (String) helper.getTag();
                    if (color.equals("silver")) {
                        color = "light_gray";
                    }
                    helper.getRootTag().put("name", "minecraft:hard_" + color + "_stained_glass_pane");
                })
                .remove("color");
    }
}
