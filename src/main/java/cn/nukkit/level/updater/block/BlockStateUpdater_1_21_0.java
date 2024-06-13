package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.function.Function;

public class BlockStateUpdater_1_21_0 implements Updater {
    public static final Updater INSTANCE = new BlockStateUpdater_1_21_0();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 21, 0)
                .match("name", "minecraft:coral_block")
                .edit("states", helper -> {
                    String type = (String) helper.getCompoundTag().remove("coral_color");
                    Object bit = helper.getCompoundTag().remove("dead_bit");
                    boolean dead = bit instanceof Byte && (byte) bit == 1 || bit instanceof Boolean && (boolean) bit;

                    String newName = switch (type) {
                        case "blue" -> "minecraft:" + (dead ? "dead_" : "") + "tube_coral_block";
                        case "pink" -> "minecraft:" + (dead ? "dead_" : "") + "brain_coral_block";
                        case "purple" -> "minecraft:" + (dead ? "dead_" : "") + "bubble_coral_block";
                        case "yellow" -> "minecraft:" + (dead ? "dead_" : "") + "horn_coral_block";
                        default -> "minecraft:" + (dead ? "dead_" : "") + "fire_coral_block";
                    };
                    helper.getRootTag().put("name", newName);
                });

        this.addTypeUpdater(ctx, "minecraft:double_plant", "double_plant_type", type -> switch (type) {
            case "syringa" -> "minecraft:lilac";
            case "grass" -> "minecraft:tall_grass";
            case "fern" -> "minecraft:large_fern";
            case "rose" -> "minecraft:rose_bush";
            case "paeonia" -> "minecraft:peony";
            default -> "minecraft:sunflower";
        });

        this.addTypeUpdater(ctx, "minecraft:stone_block_slab", "stone_slab_type", type -> switch (type) {
            case "quartz" -> "minecraft:quartz_slab";
            case "wood" -> "minecraft:petrified_oak_slab";
            case "stone_brick" -> "minecraft:stone_brick_slab";
            case "brick" -> "minecraft:brick_slab";
            case "smooth_stone" -> "minecraft:smooth_stone_slab";
            case "sandstone" -> "minecraft:sandstone_slab";
            case "nether_brick" -> "minecraft:nether_brick_slab";
            default -> "minecraft:cobblestone_slab";
        });

        this.addTypeUpdater(ctx, "minecraft:tallgrass", "tall_grass_type", type -> switch (type) {
            case "fern" -> "minecraft:fern";
            default -> "minecraft:short_grass";
        });

        // These are not official updaters
        ctx.addUpdater(1, 21, 0, false, false)
                .match("name", "minecraft:trial_spawner")
                .visit("states")
                .tryAdd("ominous", (byte) 0);

        ctx.addUpdater(1, 21, 0, false, false)
                .match("name", "minecraft:vault")
                .visit("states")
                .tryAdd("ominous", (byte) 0);
    }

    private void addTypeUpdater(CompoundTagUpdaterContext context, String identifier, String typeState, Function<String, String> rename) {
        context.addUpdater(1, 21, 0)
                .match("name", identifier)
                .visit("states")
                .edit(typeState, helper -> helper.getRootTag().put("name", rename.apply((String) helper.getTag())))
                .remove(typeState);

    }
}