package cn.powernukkitx.bootstrap.gui.exception.view;

import cn.powernukkitx.bootstrap.gui.exception.GUIException;

public abstract class ViewException extends GUIException {
    public ViewException(String message) {
        super(message);
    }

    public ViewException() {
        super();
    }
}
