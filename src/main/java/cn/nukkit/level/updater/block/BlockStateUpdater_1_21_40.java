package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_21_40 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_21_40();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        // Skull blocks was now split into individual blocks
        // however, skull type was determined by block entity or item data, and we do not have that information here
        ctx.addUpdater(1, 21, 40)
                .match("name", "minecraft:skull")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:skeleton_skull");
                });
        // these are not vanilla updaters
        // but use this one to bump the version to 18163713 as that's what vanilla does
        ctx.addUpdater(1, 21, 40)
                .match("name", "minecraft:cherry_wood")
                .visit("states")
                .remove("stripped_bit");
        ctx.addUpdater(1, 21, 40, false, false)
                .match("name", "minecraft:mangrove_wood")
                .visit("states")
                .remove("stripped_bit");
    }
}
