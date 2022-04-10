package cn.powernukkitx.bootstrap.gui.model.values;

public abstract class Warp<T> {
    private final T t;
    private boolean consumed = false;

    protected Warp(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void consume() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }
}
