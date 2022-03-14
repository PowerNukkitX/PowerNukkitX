package cn.powernukkitx.bootstrap.gui.model;

public interface DataListener<T> {
    Class<T> getDataClass();

    void handleUpdate(T data);
}
