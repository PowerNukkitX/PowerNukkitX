package cn.powernukkitx.bootstrap.gui.view.impl.update;

import cn.powernukkitx.bootstrap.gui.controller.Controller;
import cn.powernukkitx.bootstrap.gui.view.SwingView;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.keys.CheckUpdateWindowViewKey;

import javax.swing.*;

public final class CheckUpdateWindowView extends JDialog implements SwingView<JDialog> {
    private final int viewID = View.newViewID();

    @Override
    public CheckUpdateWindowViewKey getViewKey() {
        return CheckUpdateWindowViewKey.KEY;
    }

    @Override
    public int getViewID() {
        return viewID;
    }

    @Override
    public void init() {

    }

    @Override
    public JDialog getActualComponent() {
        return this;
    }

    @Override
    public Controller getController() {
        return null;
    }
}
