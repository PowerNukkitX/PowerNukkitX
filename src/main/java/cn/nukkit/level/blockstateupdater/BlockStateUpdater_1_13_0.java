package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdater_1_13_0 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_13_0();

    private static final String[] LEVER_DIRECTIONS =
            {"down_east_west", "east", "west", "south", "north", "up_north_south", "up_east_west", "down_north_south"};
    private static final String[] PILLAR_DIRECTION = {"y", "x", "z"};

    private static void registerLogUpdater(String name, String replace, CompoundTagUpdaterContext context) {
        context.addUpdater(1, 13, 0)
                .match("name", name)
                .visit("states")
                .regex("direction", "[0-2]")
                .edit("direction", helper -> {
                    int value = (int) helper.getTag();
                    helper.replaceWith("pillar_axis", PILLAR_DIRECTION[value % 3]);
                });

        context.addUpdater(1, 13, 0)
                .match("name", name)
                .visit("states")
                .regex("direction", "[3]")
                .rename(replace, "wood_type")
                .edit("direction", helper -> {
                    int value = (int) helper.getTag();
                    helper.replaceWith("pillar_axis", PILLAR_DIRECTION[value % 3]);
                })
                .addByte("stripped_bit", (byte) 0)
                .popVisit()
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:wood");
                });
    }

    private static void registerPillarUpdater(String name, CompoundTagUpdaterContext context) {
        context.addUpdater(1, 13, 0)
                .match("name", name)
                .visit("states")
                .edit("direction", helper -> {
                    int value = (int) helper.getTag();
                    helper.replaceWith("pillar_axis", PILLAR_DIRECTION[value % 3]);
                });

        context.addUpdater(1, 13, 0)
                .match("name", name)
                .visit("states")
                .tryAdd("pillar_axis", PILLAR_DIRECTION[0]);
    }

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        context.addUpdater(1, 13, 0)
                .match("name", "minecraft:lever")
                .visit("states")
                .edit("facing_direction", helper -> {
                    int value = (int) helper.getTag();
                    helper.replaceWith("lever_direction", LEVER_DIRECTIONS[value]);
                });

        registerLogUpdater("minecraft:log", "old_log_type", context);
        registerLogUpdater("minecraft:log2", "new_log_type", context);

        registerPillarUpdater("minecraft:log", context);
        registerPillarUpdater("minecraft:quartz_block", context);
        registerPillarUpdater("minecraft:log2", context);
        registerPillarUpdater("minecraft:purpur_block", context);
        registerPillarUpdater("minecraft:bone_block", context);
        registerPillarUpdater("minecraft:stripped_spruce_log", context);
        registerPillarUpdater("minecraft:stripped_birch_log", context);
        registerPillarUpdater("minecraft:stripped_jungle_log", context);
        registerPillarUpdater("minecraft:stripped_acacia_log", context);
        registerPillarUpdater("minecraft:stripped_dark_oak_log", context);
        registerPillarUpdater("minecraft:stripped_oak_log", context);
        registerPillarUpdater("minecraft:wood", context);
        registerPillarUpdater("minecraft:hay_block", context);

        context.addUpdater(1, 13, 0)
                .match("name", "minecraft:end_rod")
                .visit("states")
                .regex("facing_direction", "[^0-5]")
                .remove("facing_direction")
                .addInt("block_light_level", 14)
                .popVisit()
                .edit("name", helper -> helper.replaceWith("name", "minecraft:light_block"));

        context.addUpdater(1, 13, 0)
                .regex("name", "minecraft:.+")
                .visit("states")
                .edit("facing_direction", helper -> {
                    int value = (int) helper.getTag();
                    if (value >= 6) {
                        helper.replaceWith("facing_direction", 0);
                    }
                });

        context.addUpdater(1, 13, 0)
                .regex("name", "minecraft:.+")
                .visit("states")
                .edit("fill_level", helper -> {
                    int value = (int) helper.getTag();
                    if (value >= 7) {
                        helper.replaceWith("fill_level", 6);
                    }
                });
    }
}
