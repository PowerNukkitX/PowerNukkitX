package cn.powernukkitx.bootstrap.gui.exception.model;

import cn.powernukkitx.bootstrap.gui.exception.GUIException;

public abstract class ModelException extends GUIException {
    public ModelException(String message) {
        super(message);
    }

    public ModelException() {
        super();
    }
}
