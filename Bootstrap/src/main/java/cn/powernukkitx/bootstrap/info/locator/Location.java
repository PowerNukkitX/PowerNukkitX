package cn.powernukkitx.bootstrap.info.locator;

import java.io.File;

public class Location<T> {
    private File file;
    private T info;

    public Location(File file, T info) {
        this.file = file;
        this.info = info;
    }

    public File getFile() {
        return file;
    }

    public Location<T> setFile(File file) {
        this.file = file;
        return this;
    }

    public T getInfo() {
        return info;
    }

    public Location<T> setInfo(T info) {
        this.info = info;
        return this;
    }
}
