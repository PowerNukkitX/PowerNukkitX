package cn.nukkit.level.updater.item;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.Map;
import java.util.function.Function;

public class ItemUpdater_1_21_30 implements Updater {

    public static final Updater INSTANCE = new ItemUpdater_1_21_30();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {

        this.addTypeUpdater(
                context,
                "minecraft:chemistry_table",
                "chemistry_table_type",
                type -> "minecraft:" + type
        );

        this.addTypeUpdater(context, "minecraft:cobblestone_wall", "wall_block_type", type -> switch (type) {
            case "prismarine" -> "minecraft:prismarine_wall";
            case "red_sandstone" -> "minecraft:red_sandstone_wall";
            case "mossy_stone_brick" -> "minecraft:mossy_stone_brick_wall";
            case "mossy_cobblestone" -> "minecraft:mossy_cobblestone_wall";
            case "sandstone" -> "minecraft:sandstone_wall";
            case "nether_brick" -> "minecraft:nether_brick_wall";
            case "granite" -> "minecraft:granite_wall";
            case "red_nether_brick" -> "minecraft:red_nether_brick_wall";
            case "stone_brick" -> "minecraft:stone_brick_wall";
            case "end_brick" -> "minecraft:end_stone_brick_wall";
            case "brick" -> "minecraft:brick_wall";
            case "andesite" -> "minecraft:andesite_wall";
            case "diorite" -> BlockID.DIORITE_WALL;
            default -> "minecraft:cobblestone_wall";
        });

        context.addUpdater(1, 21, 30)
                .match("Name", "minecraft:colored_torch_bp")
                .edit("Name", helper -> {
                    Object block = helper.getRootTag().get("Block");

                    if (!(block instanceof Map<?, ?> map))
                        return;

                    Object states = map.get("states");

                    if (!(states instanceof Map<?, ?> statesMap))
                        return;

                    Object bit = statesMap.remove("color_bit");
                    boolean toggled = bit instanceof Byte && (byte) bit == 1 || bit instanceof Boolean && (boolean) bit;
                    helper.getRootTag().put("Name", toggled ? "minecraft:colored_torch_purple" : "minecraft:colored_torch_blue");
                });

        context.addUpdater(1, 21, 30)
                .match("Name", "minecraft:colored_torch_rg")
                .edit("Name", helper -> {
                    Object block = helper.getRootTag().get("Block");

                    if (!(block instanceof Map<?, ?> map))
                        return;

                    Object states = map.get("states");

                    if (!(states instanceof Map<?, ?> statesMap))
                        return;

                    Object bit = statesMap.remove("color_bit");
                    boolean toggled = bit instanceof Byte && (byte) bit == 1 || bit instanceof Boolean && (boolean) bit;
                    helper.getRootTag().put("Name", toggled ? "minecraft:colored_torch_red" : "minecraft:colored_torch_green");
                });

        this.addTypeUpdater(context, "minecraft:purpur_block", "chisel_type", type -> switch (type) {
            case "lines" -> "minecraft:purpur_pillar"; // chiseled, smooth were deprecated
            default -> "minecraft:purpur_block";
        });

        this.addTypeUpdater(context, "minecraft:sponge", "sponge_type", type -> switch (type) {
            case "wet" -> "minecraft:wet_sponge";
            default -> "minecraft:sponge";
        });

        this.addTypeUpdater(context, "minecraft:structure_void", "structure_void_type", type -> {
            return "minecraft:structure_void"; // air was removed
        });

        context.addUpdater(1, 21, 30)
                .match("Name", "minecraft:tnt")
                .edit("Name", helper -> {
                    Object block = helper.getRootTag().get("Block");

                    if (!(block instanceof Map<?, ?> map))
                        return;

                    Object states = map.get("states");

                    if (!(states instanceof Map<?, ?> statesMap))
                        return;

                    Object allowUnderwater = statesMap.remove("allow_underwater_bit");
                    boolean toggled = allowUnderwater instanceof Byte && (byte) allowUnderwater == 1 || allowUnderwater instanceof Boolean && (boolean) allowUnderwater;
                    helper.getRootTag().put("Name", toggled ? "minecraft:tnt" : "minecraft:underwater_tnt");
                });

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
