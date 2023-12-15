package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.OrderedUpdater;
import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;

import static cn.nukkit.level.blockstateupdater.util.OrderedUpdater.*;

public class BlockStateUpdater_1_20_30 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_20_30();

    public static final String[] COLORS = {
            "magenta",
            "pink",
            "green",
            "lime",
            "yellow",
            "black",
            "light_blue",
            "brown",
            "cyan",
            "orange",
            "red",
            "gray",
            "white",
            "blue",
            "purple",
            "silver"
    };

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        for (String color : COLORS) {
            if (color.equals("silver")) {
                this.addColorUpdater(ctx, "minecraft:stained_glass", color, "minecraft:light_gray_stained_glass");
                this.addColorUpdater(ctx, "minecraft:stained_glass_pane", color, "minecraft:light_gray_stained_glass_pane");
                this.addColorUpdater(ctx, "minecraft:concrete_powder", color, "minecraft:light_gray_concrete_powder");
                this.addColorUpdater(ctx, "minecraft:stained_hardened_clay", color, "minecraft:light_gray_terracotta");
            } else {
                this.addColorUpdater(ctx, "minecraft:stained_glass", color, "minecraft:" + color + "_stained_glass");
                this.addColorUpdater(ctx, "minecraft:stained_glass_pane", color, "minecraft:" + color + "_stained_glass_pane");
                this.addColorUpdater(ctx, "minecraft:concrete_powder", color, "minecraft:" + color + "_concrete_powder");
                this.addColorUpdater(ctx, "minecraft:stained_hardened_clay", color, "minecraft:" + color + "_terracotta");
            }
        }

        this.addDirectionUpdater(ctx, "minecraft:amethyst_cluster", FACING_TO_BLOCK);
        this.addDirectionUpdater(ctx, "minecraft:medium_amethyst_bud", FACING_TO_BLOCK);
        this.addDirectionUpdater(ctx, "minecraft:large_amethyst_bud", FACING_TO_BLOCK);
        this.addDirectionUpdater(ctx, "minecraft:small_amethyst_bud", FACING_TO_BLOCK);

        this.addDirectionUpdater(ctx, "minecraft:blast_furnace", FACING_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:furnace", FACING_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:lit_blast_furnace", FACING_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:lit_furnace", FACING_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:lit_smoker", FACING_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:smoker", FACING_TO_CARDINAL);

        this.addDirectionUpdater(ctx, "minecraft:anvil", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:big_dripleaf", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:calibrated_sculk_sensor", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:campfire", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:end_portal_frame", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:lectern", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:pink_petals", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:powered_comparator", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:powered_repeater", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:small_dripleaf_block", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:soul_campfire", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:unpowered_comparator", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:unpowered_repeater", DIRECTION_TO_CARDINAL);

        ctx.addUpdater(1, 20, 30)
                .regex("name", "minecraft:.+slab(?:[2-4])?\\b")
                .visit("states")
                .edit("top_slot_bit", helper -> {
                    boolean value;
                    if (helper.getTag() instanceof Byte) {
                        value = (byte) helper.getTag() == 1;
                    } else {
                        value = (boolean) helper.getTag();
                    }

                    if (value) {
                        helper.replaceWith("minecraft:vertical_half", "top");
                    } else {
                        helper.replaceWith("minecraft:vertical_half", "bottom");
                    }
                });

        // TODO: Mojang added 51 updaters, I managed to do the same with less. Maybe I missed something? Need to check later.
    }

    private void addColorUpdater(CompoundTagUpdaterContext context, String identifier, String color, String newIdentifier) {
        context.addUpdater(1, 20, 30)
                .match("name", identifier)
                .visit("states")
                .match("color", color)
                .edit("color", helper -> helper.getRootTag().put("name", newIdentifier))
                .remove("color");
    }

    private void addDirectionUpdater(CompoundTagUpdaterContext ctx, String identifier, OrderedUpdater updater) {
        ctx.addUpdater(1, 20, 30)
                .match("name", identifier)
                .visit("states")
                .edit(updater.getOldProperty(), helper -> {
                    int value = (int) helper.getTag();
                    helper.replaceWith(updater.getNewProperty(), updater.translate(value));
                });
    }
}
