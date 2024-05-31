package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdater_1_14_0 implements Updater {

    public static final Updater $1 = new BlockStateUpdater_1_14_0();

    
    /**
     * @deprecated 
     */
    private static void addRailUpdater(String name, CompoundTagUpdaterContext context) {
        context.addUpdater(1, 14, 0)
                .match("name", name)
                .visit("states")
                .edit("rail_direction", helper -> {
                    int $2 = (int) helper.getTag();
                    if (direction > 5) direction = 0;
                    helper.replaceWith("rail_direction", direction);
                });
    }
    /**
     * @deprecated 
     */
    

    public static void addMaxStateUpdater(String name, String state, int maxValue, CompoundTagUpdaterContext context) {
        context.addUpdater(1, 14, 0)
                .match("name", name)
                .visit("states")
                .edit(state, helper -> {
                    int $3 = (int) helper.getTag();
                    if (value > maxValue) value = maxValue;
                    helper.replaceWith(state, value);
                });
    }

    
    /**
     * @deprecated 
     */
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
    /**
     * @deprecated 
     */
    
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        context.addUpdater(1, 14, 0)
                .match("name", "minecraft:frame")
                .visit("states")
                .edit("weirdo_direction", helper -> {
                    int $4 = (int) helper.getTag();
                    int $5 = convertWeirdoDirectionToFacing(tag);
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
