package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdater_1_12_0 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_12_0();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        context.addUpdater(1, 12, 0)
                .match("name", "minecraft:coral_fan")
                .visit("states")
                .rename("direction", "coral_fan_direction");

        context.addUpdater(1, 12, 0)
                .match("name", "minecraft:coral_fan_dead")
                .visit("states")
                .rename("direction", "coral_fan_direction");
    }
}
