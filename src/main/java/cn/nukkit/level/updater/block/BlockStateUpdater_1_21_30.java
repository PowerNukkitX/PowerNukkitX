package cn.nukkit.level.updater.block;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.Map;
import java.util.function.Function;

public class BlockStateUpdater_1_21_30 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_21_30();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        this.addTypeUpdater(ctx, "minecraft:chemistry_table", "chemistry_table_type", type -> "minecraft:" + type);

        this.addTypeUpdater(ctx, "minecraft:cobblestone_wall", "wall_block_type", type -> switch (type) {
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

        ctx.addUpdater(1, 21, 30)
                .match("name", "minecraft:colored_torch_bp")
                .edit("states", helper -> {
                    Map<String, Object> states = helper.getCompoundTag();
                    Object bit = states.remove("color_bit");
                    boolean toggled = bit instanceof Byte && (byte) bit == 1 || bit instanceof Boolean && (boolean) bit;
                    helper.getRootTag().put("name", toggled ? "minecraft:colored_torch_purple" : "minecraft:colored_torch_blue");
                });

        ctx.addUpdater(1, 21, 30)
                .match("name", "minecraft:colored_torch_rg")
                .edit("states", helper -> {
                    Map<String, Object> states = helper.getCompoundTag();
                    Object bit = states.remove("color_bit");
                    boolean toggled = bit instanceof Byte && (byte) bit == 1 || bit instanceof Boolean && (boolean) bit;
                    helper.getRootTag().put("name", toggled ? "minecraft:colored_torch_red" : "minecraft:colored_torch_green");
                });

        this.addTypeUpdater(ctx, "minecraft:purpur_block", "chisel_type", type -> switch (type) {
            case "lines" -> "minecraft:purpur_pillar"; // chiseled, smooth were deprecated
            default -> "minecraft:purpur_block";
        });

        this.addTypeUpdater(ctx, "minecraft:sponge", "sponge_type", type -> switch (type) {
            case "wet" -> "minecraft:wet_sponge";
            default -> "minecraft:sponge";
        });

        this.addTypeUpdater(ctx, "minecraft:structure_void", "structure_void_type", type -> {
            return "minecraft:structure_void"; // air was removed
        });

        ctx.addUpdater(1, 21, 30)
                .match("name", "minecraft:tnt")
                .edit("states", helper -> {
                    Map<String, Object> states = helper.getCompoundTag();
                    Object allowUnderwater = states.remove("allow_underwater_bit");
                    boolean toggled = allowUnderwater instanceof Byte && (byte) allowUnderwater == 1 || allowUnderwater instanceof Boolean && (boolean) allowUnderwater;
                    helper.getRootTag().put("name", toggled ? "minecraft:tnt" : "minecraft:underwater_tnt");
                });


    }

    private void addTypeUpdater(CompoundTagUpdaterContext context, String identifier, String typeState, Function<String, String> rename) {
        context.addUpdater(1, 21, 30)
                .match("name", identifier)
                .visit("states")
                .edit(typeState, helper -> helper.getRootTag().put("name", rename.apply((String) helper.getTag())))
                .remove(typeState);

    }
}
