package cn.nukkit.utils;

import lombok.experimental.UtilityClass;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

/**
 * @author Kaooot
 */
@UtilityClass
public class NbtHelper {

    public NbtMap remove(NbtMap nbtMap, String key) {
        final NbtMapBuilder builder = nbtMap.toBuilder();
        builder.remove(key);
        return builder.build();
    }

    public NbtMap remove(NbtMap nbtMap, String... keys) {
        NbtMapBuilder builder = nbtMap.toBuilder();
        for (String key : keys) {
            builder.remove(key);
        }
        return builder.build();
    }
}
