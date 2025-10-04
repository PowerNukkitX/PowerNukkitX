package cn.nukkit.level.updater.item;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.Map;
import java.util.function.Function;

public class ItemUpdater_1_21_0 implements Updater {
    public static final Updater INSTANCE = new ItemUpdater_1_21_0();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 21, 0)
                .match("Name", "minecraft:coral_block")
                .edit("Name", helper -> {
                    Object block = helper.getRootTag().get("Block");
                    if (block instanceof Map map) {
                        Object states = map.get("states");
                        if (states instanceof Map statesMap) {
                            String type = statesMap.get("coral_color").toString();
                            Object bit = statesMap.get("dead_bit");
                            boolean dead = bit instanceof Byte && (byte) bit == 1 || bit instanceof Boolean && (boolean) bit;
                            String newName = switch (type) {
                                case "blue" -> "minecraft:" + (dead ? "dead_" : "") + "tube_coral_block";
                                case "pink" -> "minecraft:" + (dead ? "dead_" : "") + "brain_coral_block";
                                case "purple" -> "minecraft:" + (dead ? "dead_" : "") + "bubble_coral_block";
                                case "yellow" -> "minecraft:" + (dead ? "dead_" : "") + "horn_coral_block";
                                default -> "minecraft:" + (dead ? "dead_" : "") + "fire_coral_block";
                            };
                            helper.getRootTag().put("Name", newName);
                        }
                    }
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
    }

    private void addTypeUpdater(CompoundTagUpdaterContext context, String identifier, String typeState, Function<String, String> rename) {
        context.addUpdater(1, 21, 0)
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
