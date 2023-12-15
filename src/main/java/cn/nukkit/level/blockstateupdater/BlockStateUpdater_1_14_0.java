package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdater_1_14_0 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_14_0();

    private static void addRailUpdater(String name, CompoundTagUpdaterContext context) {
        context.addUpdater(1, 14, 0)
                .match("name", name)
                .visit("states")
                .edit("rail_direction", helper -> {
                    int direction = (int) helper.getTag();
                    if (direction > 5) direction = 0;
                    helper.replaceWith("rail_direction", direction);
                });
    }

    public static void addMaxStateUpdater(String name, String state, int maxValue, CompoundTagUpdaterContext context) {
        context.addUpdater(1, 14, 0)
                .match("name", name)
                .visit("states")
                .edit(state, helper -> {
                    int value = (int) helper.getTag();
                    if (value > maxValue) value = maxValue;
                    helper.replaceWith(state, value);
                });
    }

    private static int convertWeirdoDirectionToFacing(int weirdoDirection) {
        switch (weirdoDirection) {
            case 0:
                return 5;
            case 1:
                return 4;
            case 2:
                return 3;
            case 3:
            default:
                return 2;
        }
    }

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        context.addUpdater(1, 14, 0)
                .match("name", "minecraft:frame")
                .visit("states")
                .edit("weirdo_direction", helper -> {
                    int tag = (int) helper.getTag();
                    int newDirection = convertWeirdoDirectionToFacing(tag);
                    helper.replaceWith("facing_direction", newDirection);
                });

        addRailUpdater("minecraft:golden_rail", context);
        addRailUpdater("minecraft:detector_rail", context);
        addRailUpdater("minecraft:activator_rail", context);

        addMaxStateUpdater("minecraft:rail", "rail_direction", 9, context);
        addMaxStateUpdater("minecraft:cake", "bite_counter", 6, context);
        addMaxStateUpdater("minecraft:chorus_flower", "age", 5, context);
        addMaxStateUpdater("minecraft:cocoa", "age", 2, context);
        addMaxStateUpdater("minecraft:composter", "composter_fill_level", 8, context);
    }
}
