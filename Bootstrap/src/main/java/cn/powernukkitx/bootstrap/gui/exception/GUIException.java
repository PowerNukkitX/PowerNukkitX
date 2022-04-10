package cn.powernukkitx.bootstrap.gui.exception;

public abstract class GUIException extends RuntimeException {
    public GUIException(String message) {
        super(message);
    }

    public GUIException() {
        super();
    }
}
