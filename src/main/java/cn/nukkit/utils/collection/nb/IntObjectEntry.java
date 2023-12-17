package cn.nukkit.utils.collection.nb;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.Map;


public interface IntObjectEntry<V> extends Map.Entry<Integer, V> {
    @Deprecated
    default Integer getKey() {
        return getIntKey();
    }

    int getIntKey();
}
