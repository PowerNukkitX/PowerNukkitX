package cn.powernukkitx.bootstrap.gui.controller;

import cn.powernukkitx.bootstrap.gui.model.impl.view.MainWindowViewModel;
import cn.powernukkitx.bootstrap.gui.model.keys.MainWindowDataKeys;
import cn.powernukkitx.bootstrap.gui.view.MainWindowView;
import cn.powernukkitx.bootstrap.gui.view.View;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MainWindowController extends CommonController {
    public static final List<Class<? extends View>> VIEW_CLASSES = Collections.singletonList(MainWindowView.class);

    private final List<MainWindowView> aliveViews = new ArrayList<>(1);

    private final MainWindowViewModel mainWindowViewModel;
    private final MainWindowView mainWindowView;

    public MainWindowController() {
        super();
        addModel(this.mainWindowViewModel = new MainWindowViewModel());
        this.mainWindowView = new MainWindowView(this);
    }

    @Override
    public void start() {
        aliveViews.add(mainWindowView);
        this.mainWindowView.init();
    }

    @Override
    public List<Class<? extends View>> getAvailableViews() {
        return VIEW_CLASSES;
    }

    @Override
    public List<MainWindowView> getAliveViews() {
        return this.aliveViews;
    }

    @Override
    public void addView(View view) {
        if (view instanceof MainWindowView)
            aliveViews.add((MainWindowView) view);
    }

    public MainWindowView getMainWindowView() {
        return mainWindowView;
    }

    public MainWindowViewModel getMainWindowViewModel() {
        return mainWindowViewModel;
    }

    public void onResize(Dimension d) {
        mainWindowViewModel.setData(MainWindowDataKeys.WINDOW_SIZE, d);
    }
}
