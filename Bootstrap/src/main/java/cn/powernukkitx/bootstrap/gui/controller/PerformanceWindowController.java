package cn.powernukkitx.bootstrap.gui.controller;

import cn.powernukkitx.bootstrap.gui.model.impl.view.PerformanceWindowModel;
import cn.powernukkitx.bootstrap.gui.model.keys.PerformanceDataKeys;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.impl.monitor.PerformanceView;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public final class PerformanceWindowController extends CommonController{
    private static final List<Class<? extends View<?>>> VIEW_CLASS = Collections.singletonList(PerformanceView.class);

    private final PerformanceView performanceView;
    private final PerformanceWindowModel performanceWindowModel;

    public PerformanceWindowController() {
        this.performanceView = new PerformanceView(this);
        this.performanceWindowModel = new PerformanceWindowModel();
        this.views.add(performanceView);
        this.models.add(performanceWindowModel);
    }

    @Override
    public List<Class<? extends View<?>>> getAvailableViews() {
        return VIEW_CLASS;
    }

    @Override
    public void start() {
        performanceWindowModel.init();
        SwingUtilities.invokeLater(performanceView::init);
    }

    public PerformanceWindowModel getModel() {
        return performanceWindowModel;
    }

    public void onOpen() {
        this.batchSetData(PerformanceDataKeys.DISPLAY, true);
    }

    public void onClose() {
        performanceWindowModel.setData(PerformanceDataKeys.DISPLAY, false);
    }
}
