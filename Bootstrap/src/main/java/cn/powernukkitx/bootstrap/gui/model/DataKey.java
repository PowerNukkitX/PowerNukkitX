package cn.powernukkitx.bootstrap.gui.model;

import java.util.Objects;

public abstract class DataKey<T> {
    protected final EnumDataKey id;
    protected final Class<T> clazz;

    protected DataKey(EnumDataKey id, Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataKey<?> dataKey = (DataKey<?>) o;
        return id == dataKey.id && Objects.equals(clazz, dataKey.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clazz);
    }

    public EnumDataKey getId() {
        return id;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
