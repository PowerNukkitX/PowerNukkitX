package org.powernukkitx.nbt.tag;

import java.util.Objects;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class NumberTag<T extends Number> extends Tag {
    public abstract T getData();

    public abstract void setData(T data);

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NumberTag<?> other && getId() == other.getId() && Objects.equals(getData(), other.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getData());
    }
}
