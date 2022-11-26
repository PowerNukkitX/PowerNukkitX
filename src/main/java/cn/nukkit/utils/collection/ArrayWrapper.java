package cn.nukkit.utils.collection;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.40-r4")
public interface ArrayWrapper<T> {
    @ShouldThaw
    T[] getRawData();

    @ShouldThaw
    T get(int index);

    @ShouldThaw
    void set(int index, T t);
}
