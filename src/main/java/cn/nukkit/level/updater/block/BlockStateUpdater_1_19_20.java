package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_19_20 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_19_20();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        this.addProperty(context, "minecraft:muddy_mangrove_roots", "pillar_axis", "y");
    }

    private void addProperty(CompoundTagUpdaterContext context, String identifier, String propertyName, Object value) {
        context.addUpdater(1, 18, 10, true) // Here we go again Mojang
                .match("name", identifier)
                .visit("states")
                .tryAdd(propertyName, value);
    }
}
