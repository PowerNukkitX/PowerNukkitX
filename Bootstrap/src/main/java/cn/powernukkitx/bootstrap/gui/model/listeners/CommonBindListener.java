package cn.powernukkitx.bootstrap.gui.model.listeners;

import cn.powernukkitx.bootstrap.gui.model.DataListener;
import cn.powernukkitx.bootstrap.gui.view.BindHandler;

public final class CommonBindListener<T> implements DataListener<T> {
    private final Class<T> clazz;
    private final BindHandler<T> bindHandler;

    public CommonBindListener(Class<T> clazz, BindHandler<T> bindHandler) {
        this.clazz = clazz;
        this.bindHandler = bindHandler;
    }

    @Override
    public Class<T> getDataClass() {
        return clazz;
    }

    @Override
    public void handleUpdate(T data) {
        bindHandler.handle(data);
    }
}
