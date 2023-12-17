package cn.nukkit.utils.collection.nb;

import java.util.Map;


public interface LongObjectEntry<V> extends Map.Entry<Long, V> {
    @Deprecated
    default Long getKey() {
        return getLongKey();
    }

    long getLongKey();
}
