package cn.nukkit.utils.collection;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.50-r1")
public interface ArrayWrapper<T> {
    @ShouldThaw
    T[] getRawData();

    @ShouldThaw
    void setRawData(T[] data);

    @ShouldThaw
    T get(int index);

    @ShouldThaw
    void set(int index, T t);
}
