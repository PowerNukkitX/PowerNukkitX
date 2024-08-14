package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.function.Function;

public class BlockStateUpdater_1_21_20 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_21_20();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 21, 20)
                .match("name", "minecraft:light_block")
                .visit("states")
                .edit("block_light_level", helper -> helper.getRootTag().put("name", "minecraft:light_block_" + helper.getTag()))
                .remove("block_light_level");


        this.addTypeUpdater(ctx, "minecraft:sandstone", "sand_stone_type", type -> switch (type) {
            case "cut" -> "minecraft:cut_sandstone";
            case "heiroglyphs" -> "minecraft:chiseled_sandstone";
            case "smooth" -> "minecraft:smooth_sandstone";
            default -> "minecraft:sandstone";
        });

        this.addTypeUpdater(ctx, "minecraft:quartz_block", "chisel_type", type -> switch (type) {
            case "chiseled" -> "minecraft:chiseled_quartz_block";
            case "lines" -> "minecraft:quartz_pillar";
            case "smooth" -> "minecraft:smooth_quartz";
            default -> "minecraft:quartz_block";
        });

        this.addTypeUpdater(ctx, "minecraft:red_sandstone", "sand_stone_type", type -> switch (type) {
            case "cut" -> "minecraft:cut_red_sandstone";
            case "heiroglyphs" -> "minecraft:chiseled_red_sandstone";
            case "smooth" -> "minecraft:smooth_red_sandstone";
            default -> "minecraft:red_sandstone";
        });

        this.addTypeUpdater(ctx, "minecraft:sand", "sand_type", type -> switch (type) {
            case "red" -> "minecraft:red_sand";
            default -> "minecraft:sand";
        });

        this.addTypeUpdater(ctx, "minecraft:dirt", "dirt_type", type -> switch (type) {
            case "coarse" -> "minecraft:coarse_dirt";
            default -> "minecraft:dirt";
        });

        this.addTypeUpdater(ctx, "minecraft:anvil", "damage", type -> switch (type) {
            case "broken" -> "minecraft:damaged_anvil";
            case "slightly_damaged" -> "minecraft:chipped_anvil";
            case "very_damaged" -> "minecraft:deprecated_anvil";
            default -> "minecraft:anvil";
        });

        // Vanilla does not use updater for this block for some reason
        ctx.addUpdater(1, 21, 20, false, false)
                .match("name", "minecraft:yellow_flower")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:dandelion");
                });
    }

    private void addTypeUpdater(CompoundTagUpdaterContext context, String identifier, String typeState, Function<String, String> rename) {
        context.addUpdater(1, 21, 20)
                .match("name", identifier)
                .visit("states")
                .edit(typeState, helper -> helper.getRootTag().put("name", rename.apply((String) helper.getTag())))
                .remove(typeState);

    }
}
