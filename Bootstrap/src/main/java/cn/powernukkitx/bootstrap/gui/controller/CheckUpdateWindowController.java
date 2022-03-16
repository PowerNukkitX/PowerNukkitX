package cn.powernukkitx.bootstrap.gui.controller;

import cn.powernukkitx.bootstrap.gui.model.impl.view.UpdateWindowModel;
import cn.powernukkitx.bootstrap.gui.model.keys.UpdateWindowDataKeys;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.impl.update.CheckUpdateWindowView;
import cn.powernukkitx.bootstrap.gui.view.keys.CheckUpdateWindowViewKey;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public final class CheckUpdateWindowController extends CommonController {
    private static final List<Class<? extends View<?>>> VIEW_CLASS = Collections.singletonList(CheckUpdateWindowView.class);

    private final CheckUpdateWindowView checkUpdateWindowView;
    private final UpdateWindowModel updateWindowModel;

    public CheckUpdateWindowController() {
        super();
        this.checkUpdateWindowView = new CheckUpdateWindowView(this);
        this.updateWindowModel = new UpdateWindowModel();
        this.views.add(checkUpdateWindowView);
        this.models.add(updateWindowModel);
    }

    @Override
    public List<Class<? extends View<?>>> getAvailableViews() {
        return VIEW_CLASS;
    }

    @Override
    public void start() {
        SwingUtilities.invokeLater(checkUpdateWindowView::init);
    }

    public void onResize(Dimension d) {
        updateWindowModel.setData(UpdateWindowDataKeys.WINDOW_SIZE, d);
    }

    public void onClose() {
        updateWindowModel.setData(UpdateWindowDataKeys.DISPLAY, false);
    }
}
