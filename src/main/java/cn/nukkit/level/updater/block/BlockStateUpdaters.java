package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class BlockStateUpdaters {

    private static final CompoundTagUpdaterContext CONTEXT;
    private static final int LATEST_VERSION;

    static {
        List<Updater> updaters = new ArrayList<>();
        updaters.add(BlockStateUpdaterBase.INSTANCE);
        updaters.add(BlockStateUpdater_1_10_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_12_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_13_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_14_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_15_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_16_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_16_210.INSTANCE);
        updaters.add(BlockStateUpdater_1_17_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_17_40.INSTANCE);
        updaters.add(BlockStateUpdater_1_18_10.INSTANCE);
        updaters.add(BlockStateUpdater_1_18_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_20.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_70.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_80.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_10.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_40.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_50.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_60.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_70.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_80.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_0.INSTANCE);

        CompoundTagUpdaterContext context = new CompoundTagUpdaterContext();
        updaters.forEach(updater -> updater.registerUpdaters(context));
        CONTEXT = context;
        LATEST_VERSION = context.getLatestVersion();
    }

    public static CompoundTag updateBlockState(CompoundTag tag, int version) {
        return CONTEXT.update(tag, version);
    }

    public static void serializeCommon(Map<String, Object> builder, String id) {
        builder.put("version", LATEST_VERSION);
        builder.put("name", id);
    }

    public static int getLatestVersion() {
        return LATEST_VERSION;
    }
}
