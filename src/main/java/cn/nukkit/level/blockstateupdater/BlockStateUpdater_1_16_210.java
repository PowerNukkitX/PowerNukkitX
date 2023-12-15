package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdater_1_16_210 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_16_210();

    private static void registerUpdater(CompoundTagUpdaterContext context, String name) {
        context.addUpdater(1, 16, 210)
                .match("name", name)
                .visit("states")
                .remove("deprecated");
    }

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        registerUpdater(context, "minecraft:stripped_crimson_stem");
        registerUpdater(context, "minecraft:stripped_warped_stem");
        registerUpdater(context, "minecraft:stripped_crimson_hyphae");
        registerUpdater(context, "minecraft:stripped_warped_hyphae");
    }
}
