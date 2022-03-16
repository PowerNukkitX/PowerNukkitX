package cn.powernukkitx.bootstrap.gui.controller;

import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.impl.update.CheckUpdateWindowView;

import java.util.Collections;
import java.util.List;

public final class CheckUpdateWindowController extends CommonController {
    private static final List<Class<? extends View<?>>> VIEW_CLASS = Collections.singletonList(CheckUpdateWindowView.class);

    public CheckUpdateWindowController() {
        super();
    }

    @Override
    public List<Class<? extends View<?>>> getAvailableViews() {
        return VIEW_CLASS;
    }

    @Override
    public void start() {

    }
}
