package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_19_70 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_19_70();

    public static final String[] COLORS = {
            "magenta",
            "pink",
            "green",
            "lime",
            "yellow",
            "black",
            "light_blue",
            "brown",
            "cyan",
            "orange",
            "red",
            "gray",
            "white",
            "blue",
            "purple",
            "silver"
    };

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        for (String color : COLORS) {
            this.addColorUpdater(context, color);
        }
    }

    private void addColorUpdater(CompoundTagUpdaterContext context, String color) {
        context.addUpdater(1, 19, 70)
                .match("name", "minecraft:wool")
                .visit("states")
                .match("color", color)
                .edit("color", helper -> {
                    if (color.equals("silver")) {
                        helper.getRootTag().put("name", "minecraft:light_gray_wool");
                    } else {
                        helper.getRootTag().put("name", "minecraft:" + color + "_wool");
                    }
                })
                .remove("color");

    }
}
