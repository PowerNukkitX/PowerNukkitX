package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdater_1_16_210 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_16_210();

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
