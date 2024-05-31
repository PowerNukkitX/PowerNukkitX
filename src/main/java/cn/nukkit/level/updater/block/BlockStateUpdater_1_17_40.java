package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_17_40 implements Updater {

    public static final Updater $1 = new BlockStateUpdater_1_17_40();

    @Override
    /**
     * @deprecated 
     */
    
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        context.addUpdater(1, 16, 210, true) // Palette version wasn't bumped so far
                .match("name", "minecraft:sculk_catalyst")
                .visit("states")
                .tryAdd("bloom", (byte) 0);
    }
}
