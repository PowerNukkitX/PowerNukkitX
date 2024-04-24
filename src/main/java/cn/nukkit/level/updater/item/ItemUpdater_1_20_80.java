package cn.nukkit.level.updater.item;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.Map;
import java.util.function.Function;

public class ItemUpdater_1_20_80 implements Updater {
    public static final Updater INSTANCE = new ItemUpdater_1_20_80();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        this.addTypeUpdater(ctx, "minecraft:sapling", "sapling_type", type -> "minecraft:" + type + "_sapling");
        this.addTypeUpdater(ctx, "minecraft:red_flower", "flower_type", type -> switch (type) {
            case "tulip_orange" -> "minecraft:orange_tulip";
            case "tulip_pink" -> "minecraft:pink_tulip";
            case "tulip_white" -> "minecraft:white_tulip";
            case "tulip_red" -> "minecraft:red_tulip";
            case "oxeye" -> "minecraft:oxeye_daisy";
            case "orchid" -> "minecraft:blue_orchid";
            case "houstonia" -> "minecraft:azure_bluet";
            default -> "minecraft:" + type;
        });
        this.addTypeUpdater(ctx, "minecraft:coral_fan", "coral_color", type -> switch (type) {
            case "blue" -> "minecraft:tube_coral_fan";
            case "pink" -> "minecraft:brain_coral_fan";
            case "purple" -> "minecraft:bubble_coral_fan";
            case "yellow" -> "minecraft:horn_coral_fan";
            default -> "minecraft:fire_coral_fan";
        });
        this.addTypeUpdater(ctx, "minecraft:coral_fan_dead", "coral_color", type -> switch (type) {
            case "blue" -> "minecraft:dead_tube_coral_fan";
            case "pink" -> "minecraft:dead_brain_coral_fan";
            case "purple" -> "minecraft:dead_bubble_coral_fan";
            case "yellow" -> "minecraft:dead_horn_coral_fan";
            default -> "minecraft:dead_fire_coral_fan";
        });
    }

    private void addTypeUpdater(CompoundTagUpdaterContext context, String identifier, String typeState, Function<String, String> rename) {
        context.addUpdater(1, 20, 80)
                .match("Name", identifier)
                .edit("Name", helper -> {
                    Object block = helper.getRootTag().get("Block");
                    if (block instanceof Map map) {
                        Object states = map.get("states");
                        if (states instanceof Map statesMap) {
                            Object tag = statesMap.get(typeState);
                            if (tag instanceof String string) {
                                helper.getRootTag().put("Name", rename.apply(string));
                            }
                        }
                    }
                });
    }
}
