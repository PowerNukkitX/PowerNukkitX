package cn.nukkit.level.format.leveldb.util;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.nbt.tag.CompoundTag;

public final class LDBBlockUtils {
    public static CompoundTag blockState2Nbt(BlockState blockState) {
        var tag = new CompoundTag()
                .putString("name", blockState.getPersistenceName())
                .putInt("version", blockState.getVersion());
        var properties = blockState.getProperties();
        for(var each:properties.getNames()) {
            // TODO: 2022/3/21 完成nbt数据值到blockstate的映射
        }
        return tag;
    }
}
