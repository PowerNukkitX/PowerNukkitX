package cn.powernukkitx.bootstrap.gui.exception.view;

import cn.powernukkitx.bootstrap.gui.view.View;

public final class InvalidViewClassException extends ViewException {
    private final View view;

    public InvalidViewClassException(View view) {
        super("View " + view.getViewID() + " (" + view.getViewKey() + ")");
        this.view = view;
    }

    public View getView() {
        return view;
    }
}
