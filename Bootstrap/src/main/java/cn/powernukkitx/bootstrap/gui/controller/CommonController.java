package cn.powernukkitx.bootstrap.gui.controller;

import cn.powernukkitx.bootstrap.gui.model.Model;
import cn.powernukkitx.bootstrap.gui.model.impl.CommonModel;
import cn.powernukkitx.bootstrap.gui.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CommonController implements Controller{
    protected final List<View<?>> views = new ArrayList<>();
    protected final List<Model> models = new ArrayList<>();

    public CommonController() {

    }

    @Override
    public List<Class<? extends View<?>>> getAvailableViews() {
        return Collections.emptyList();
    }

    @Override
    public List<? extends View<?>> getAliveViews() {
        return views;
    }

    @Override
    public void addView(View<?> view) {
        views.add(view);
    }

    @Override
    public List<Model> getRawModelList() {
        return models;
    }
}
