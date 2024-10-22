package cn.nukkit.level.updater.item;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.Map;
import java.util.function.Function;

public class ItemUpdater_1_21_40 implements Updater {

    public static final Updater INSTANCE = new ItemUpdater_1_21_40();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        // Skull blocks was now split into individual blocks
        // however, skull type was determined by block entity or item data, and we do not have that information here
        context.addUpdater(1, 21, 40)
                .match("Name", "minecraft:skull")
                .edit("Name", helper -> {
                    helper.replaceWith("Name", "minecraft:skeleton_skull");
                });
        // these are not vanilla updaters
        // but use this one to bump the version to 18163713 as that's what vanilla does
        context.addUpdater(1, 21, 40)
                .match("Name", "minecraft:cherry_wood")
                .visit("Block")
                .visit("states")
                .remove("stripped_bit");
        context.addUpdater(1, 21, 40, false, false)
                .match("Name", "minecraft:mangrove_wood")
                .visit("Block")
                .visit("states")
                .remove("stripped_bit");
    }

    private void addTypeUpdater(CompoundTagUpdaterContext context, String identifier, String typeState, Function<String, String> rename) {
        context.addUpdater(1, 21, 30)
                .match("Name", identifier)
                .edit("Name", helper -> {
                    Object block = helper.getRootTag().get("Block");
                    if (block instanceof Map<?, ?> map) {
                        Object states = map.get("states");
                        if (states instanceof Map<?, ?> statesMap) {
                            Object tag = statesMap.get(typeState);
                            if (tag instanceof String string) {
                                helper.getRootTag().put("Name", rename.apply(string));
                            }
                        }
                    }
                });
    }

}
