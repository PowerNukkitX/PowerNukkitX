package cn.nukkit.level.updater.item;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.Map;
import java.util.function.Function;

public class ItemUpdater_1_21_20 implements Updater {

    public static final Updater INSTANCE = new ItemUpdater_1_21_20();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {

        this.addTypeUpdater(ctx, "minecraft:dirt", "dirt_type", type -> switch (type) {
            case "coarse" -> "minecraft:coarse_dirt";
            default -> "minecraft:dirt";
        });


        this.addTypeUpdater(ctx, "minecraft:stone_block_slab", "stone_slab_type", type -> switch (type) {
            case "sandstone" -> "minecraft:sandstone_slab";
            case "wood" -> "minecraft:oak_slab";
            case "cobblestone" -> "minecraft:cobblestone_slab";
            case "brick" -> "minecraft:brick_slab";
            case "stone_brick" -> "minecraft:stone_brick_slab";
            case "quartz" -> "minecraft:quartz_slab";
            case "nether_brick" -> "minecraft:nether_brick_slab";
            default -> "minecraft:smooth_stone_slab";
        });

        this.addTypeUpdater(ctx, "minecraft:double_stone_block_slab", "stone_slab_type", type -> switch (type) {
            case "sandstone" -> "minecraft:sandstone_double_slab";
            case "wood" -> "minecraft:oak_double_slab";
            case "cobblestone" -> "minecraft:cobblestone_double_slab";
            case "brick" -> "minecraft:brick_double_slab";
            case "stone_brick" -> "minecraft:stone_brick_double_slab";
            case "quartz" -> "minecraft:quartz_double_slab";
            case "nether_brick" -> "minecraft:nether_brick_double_slab";
            default -> "minecraft:smooth_stone_double_slab";
        });


        this.addTypeUpdater(ctx, "minecraft:stone_block_slab2", "stone_slab_type_2", type -> switch (type) {
            case "red_sandstone" -> "minecraft:red_sandstone_slab";
            case "purpur" -> "minecraft:purpur_slab";
            case "prismarine_rough" -> "minecraft:prismarine_slab";
            case "prismarine_dark" -> "minecraft:dark_prismarine_slab";
            case "prismarine_brick" -> "minecraft:prismarine_brick_slab";
            case "mossy_cobblestone" -> "minecraft:mossy_cobblestone_slab";
            case "smooth_sandstone" -> "minecraft:smooth_sandstone_slab";
            default -> "minecraft:red_nether_brick_slab";
        });

        this.addTypeUpdater(ctx, "minecraft:double_stone_block_slab2", "stone_slab_type_2", type -> switch (type) {
            case "red_sandstone" -> "minecraft:red_sandstone_double_slab";
            case "purpur" -> "minecraft:purpur_double_slab";
            case "prismarine_rough" -> "minecraft:prismarine_double_slab";
            case "prismarine_dark" -> "minecraft:dark_prismarine_double_slab";
            case "prismarine_brick" -> "minecraft:prismarine_brick_double_slab";
            case "mossy_cobblestone" -> "minecraft:mossy_cobblestone_double_slab";
            case "smooth_sandstone" -> "minecraft:smooth_sandstone_double_slab";
            default -> "minecraft:red_nether_brick_double_slab";
        });


        this.addTypeUpdater(ctx, "minecraft:stone_block_slab3", "stone_slab_type_3", type -> switch (type) {
            case "end_stone_brick" -> "minecraft:end_stone_brick_slab";
            case "smooth_red_sandstone" -> "minecraft:smooth_red_sandstone_slab";
            case "polished_andesite" -> "minecraft:polished_andesite_slab";
            case "andesite" -> "minecraft:andesite_slab";
            case "diorite" -> "minecraft:diorite_slab";
            case "polished_diorite" -> "minecraft:polished_diorite_slab";
            case "granite" -> "minecraft:granite_slab";
            default -> "minecraft:polished_granite_slab";
        });

        this.addTypeUpdater(ctx, "minecraft:double_stone_block_slab3", "stone_slab_type_3", type -> switch (type) {
            case "end_stone_brick" -> "minecraft:end_stone_brick_double_slab";
            case "smooth_red_sandstone" -> "minecraft:smooth_red_sandstone_double_slab";
            case "polished_andesite" -> "minecraft:polished_andesite_double_slab";
            case "andesite" -> "minecraft:andesite_double_slab";
            case "diorite" -> "minecraft:diorite_double_slab";
            case "polished_diorite" -> "minecraft:polished_diorite_double_slab";
            case "granite" -> "minecraft:granite_double_slab";
            default -> "minecraft:polished_granite_double_slab";
        });


        this.addTypeUpdater(ctx, "minecraft:stone_block_slab4", "stone_slab_type_4", type -> switch (type) {
            case "mossy_stone_brick" -> "minecraft:mossy_stone_brick_slab";
            case "smooth_quartz" -> "minecraft:smooth_quartz_slab";
            case "stone" -> "minecraft:normal_stone_slab";
            case "cut_sandstone" -> "minecraft:cut_sandstone_slab";
            default -> "minecraft:cut_red_sandstone_slab";
        });

        this.addTypeUpdater(ctx, "minecraft:double_stone_block_slab4", "stone_slab_type_4", type -> switch (type) {
            case "mossy_stone_brick" -> "minecraft:mossy_stone_brick_double_slab";
            case "smooth_quartz" -> "minecraft:smooth_quartz_double_slab";
            case "stone" -> "minecraft:normal_stone_double_slab";
            case "cut_sandstone" -> "minecraft:cut_sandstone_double_slab";
            default -> "minecraft:cut_red_sandstone_double_slab";
        });


        ctx.addUpdater(1, 21, 20)
                .match("Name", "minecraft:light_block")
                .edit("Name", helper -> {
                    Object block = helper.getRootTag().get("Block");

                    if (!(block instanceof Map<?, ?> map))
                        return;

                    Object states = map.get("states");

                    if (!(states instanceof Map<?, ?> statesMap))
                        return;

                    int lightLevel = (int) statesMap.get("block_light_level");

                    String newId = "minecraft:light_block_" + lightLevel;
                    helper.getRootTag().put("Name", newId);
                });


        this.addTypeUpdater(ctx, "minecraft:monster_egg", "monster_egg_stone_type", type -> switch (type) {
            case "stone" -> BlockID.INFESTED_STONE;
            case "cobblestone" -> BlockID.INFESTED_COBBLESTONE;
            case "stone_brick" -> BlockID.INFESTED_STONE_BRICKS;
            case "mossy_stone_brick" -> BlockID.INFESTED_MOSSY_STONE_BRICKS;
            case "cracked_stone_brick" -> BlockID.INFESTED_CRACKED_STONE_BRICKS;
            default -> BlockID.INFESTED_CHISELED_STONE_BRICKS;
        });


        this.addTypeUpdater(ctx, "minecraft:prismarine", "prismarine_block_type", type -> switch (type) {
            case "default" -> BlockID.PRISMARINE;
            case "dark" -> BlockID.DARK_PRISMARINE;
            default -> BlockID.PRISMARINE_BRICKS;
        });


        this.addTypeUpdater(ctx, "minecraft:quartz_block", "chisel_type", type -> switch (type) {
            case "default" -> BlockID.QUARTZ_BLOCK;
            case "chiseled" -> BlockID.CHISELED_QUARTZ_BLOCK;
            case "lines" -> BlockID.QUARTZ_PILLAR;
            default -> BlockID.SMOOTH_QUARTZ;
        });


        this.addTypeUpdater(ctx, "minecraft:red_sandstone", "sand_stone_type", type -> switch (type) {
            case "default" -> BlockID.RED_SANDSTONE;
            case "heiroglyphs" -> BlockID.CHISELED_RED_SANDSTONE;
            case "cut" -> BlockID.CUT_SANDSTONE;
            default -> BlockID.SMOOTH_SANDSTONE;
        });


        this.addTypeUpdater(ctx, "minecraft:sand", "sand_type", type -> switch (type) {
            case "normal" -> BlockID.SAND;
            default -> BlockID.RED_SAND;
        });


        this.addTypeUpdater(ctx, "minecraft:sandstone", "sandstone_type", type -> switch (type) {
            case "default" -> BlockID.SANDSTONE;
            case "heiroglyphs" -> BlockID.CHISELED_SANDSTONE;
            case "cut" -> BlockID.CUT_SANDSTONE;
            default -> BlockID.SMOOTH_SANDSTONE;
        });


        this.addTypeUpdater(ctx, "minecraft:stonebrick", "stone_brick_type", type -> switch (type) {
            case "default" -> BlockID.STONE_BRICKS;
            case "mossy" -> BlockID.MOSSY_STONE_BRICKS;
            case "cracked" -> BlockID.CRACKED_STONE_BRICKS;
            default -> BlockID.CHISELED_STONE_BRICKS;
        });


        ctx.addUpdater(1, 21, 20)
                .match("Name", "minecraft:yellow_flower")
                .edit("Name", helper -> helper.getRootTag().put("Name", BlockID.DANDELION));


        this.addTypeUpdater(ctx, "minecraft:anvil", "damage", type -> switch (type) {
            case "slightly_damaged" -> BlockID.CHIPPED_ANVIL;
            case "very_damaged" -> BlockID.DAMAGED_ANVIL;
            case "broken" -> BlockID.AIR;
            default -> BlockID.ANVIL;
        });
    }


    private void addTypeUpdater(CompoundTagUpdaterContext context, String identifier, String typeState, Function<String, String> rename) {
        context.addUpdater(1, 21, 20)
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
