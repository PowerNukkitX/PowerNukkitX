package cn.powernukkitx.bootstrap.gui.view;

import java.util.Objects;

public abstract class ViewKey<T extends View> {
    protected final EnumViewKey id;
    protected final Class<T> clazz;

    protected ViewKey(EnumViewKey id, Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewKey<?> dataKey = (ViewKey<?>) o;
        return id == dataKey.id && Objects.equals(clazz, dataKey.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clazz);
    }

    public EnumViewKey getId() {
        return id;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
