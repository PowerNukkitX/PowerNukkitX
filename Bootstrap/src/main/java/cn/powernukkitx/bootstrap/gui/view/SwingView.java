package cn.powernukkitx.bootstrap.gui.view;

import cn.powernukkitx.bootstrap.gui.model.DataKey;

import javax.swing.*;

public interface SwingView<C> extends View<C> {
    @Override
    default <T, B extends T> void bind(DataKey<B> dataKey, Class<T> dataClass, BindHandler<T> bindHandler) {
        SwingUtilities.invokeLater(() -> View.super.bind(dataKey, dataClass, bindHandler));
    }
}
