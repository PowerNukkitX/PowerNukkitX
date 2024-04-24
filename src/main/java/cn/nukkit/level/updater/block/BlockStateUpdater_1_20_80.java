package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.function.Function;

public class BlockStateUpdater_1_20_80 implements Updater {
    public static final Updater INSTANCE = new BlockStateUpdater_1_20_80();

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


        // This is not official updater, but they correctly removed sapling_type
        ctx.addUpdater(1, 20, 80, false, false)
                .match("name", "minecraft:bamboo_sapling")
                .visit("states")
                .remove("sapling_type");
    }

    private void addTypeUpdater(CompoundTagUpdaterContext context, String identifier, String typeState, Function<String, String> rename) {
        context.addUpdater(1, 20, 80)
                .match("name", identifier)
                .visit("states")
                .edit(typeState, helper -> helper.getRootTag().put("name", rename.apply((String) helper.getTag())))
                .remove(typeState);

    }
}
