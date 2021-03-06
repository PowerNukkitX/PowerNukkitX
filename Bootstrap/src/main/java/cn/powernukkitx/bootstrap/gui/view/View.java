package cn.powernukkitx.bootstrap.gui.view;

import cn.powernukkitx.bootstrap.gui.controller.Controller;
import cn.powernukkitx.bootstrap.gui.model.DataKey;
import cn.powernukkitx.bootstrap.gui.model.listeners.CommonBindListener;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;

public interface View<C> {
    AtomicInteger globalViewID = new AtomicInteger(0);

    static int newViewID() {
        return globalViewID.getAndIncrement();
    }

    ViewKey<? extends View<?>> getViewKey();

    int getViewID();

    void init();

    C getActualComponent();

    Controller getController();

    default <T, B extends T> void bind(DataKey<B> dataKey, Class<T> dataClass, BindHandler<T> bindHandler) {
        getController().getAndListenData(dataKey, new CommonBindListener<>(dataClass, bindHandler));
    }
}
