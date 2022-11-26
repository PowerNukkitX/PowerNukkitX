package cn.nukkit.utils.collection;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.40-r4")
public interface ByteArrayWrapper extends ArrayWrapper<Byte> {
    byte[] getRawBytes();

    @ShouldThaw
    byte getByte(int index);

    @ShouldThaw
    void setByte(int index, byte b);

    @Override
    @Deprecated
    @DeprecationDetails(since = "1.19.40-r3", reason = "avoid boxing", replaceWith = "getByte")
    default Byte get(int index) {
        return getByte(index);
    }

    @Override
    @Deprecated
    @DeprecationDetails(since = "1.19.40-r3", reason = "avoid unboxing", replaceWith = "setByte")
    default void set(int index, Byte b) {
        setByte(index, b);
    }

    @Override
    @Deprecated
    @DeprecationDetails(since = "1.19.40-r3", reason = "avoid unboxing", replaceWith = "getRawBytes")
    default Byte[] getRawData() {
        var tmp = getRawBytes();
        var len = tmp.length;
        var out = new Byte[len];
        for (int i = 0; i < len; i++) out[i] = tmp[i];
        return out;
    }
}
