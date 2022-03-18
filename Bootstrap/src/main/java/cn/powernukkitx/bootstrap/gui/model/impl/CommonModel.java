package cn.powernukkitx.bootstrap.gui.model.impl;

import cn.powernukkitx.bootstrap.gui.model.DataKey;
import cn.powernukkitx.bootstrap.gui.model.DataListener;
import cn.powernukkitx.bootstrap.gui.model.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommonModel implements Model {
    public static final CommonModel INSTANCE = new CommonModel(null);
    public static final Timer TIMER = new Timer("timer");

    protected final Map<DataKey<?>, Object> dataMap = new HashMap<>();
    protected final Map<DataKey<?>, List<DataListener<?>>> dataListenerMap = new HashMap<>();

    protected Model parent;
    protected final List<Model> childrenModels = new ArrayList<>();

    public CommonModel(@Nullable Model parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChildModel(this);
        }
    }

    @Override
    public String getName() {
        return "CommonModel";
    }

    @Override
    public <T> T getDataDirectly(DataKey<T> dataKey) {
        return dataKey.getClazz().cast(dataMap.get(dataKey));
    }

    @Override
    public <T> void setDataDirectly(DataKey<T> dataKey, T data) {
        dataMap.put(dataKey, data);
    }

    @Override
    public <T, B extends T> void addDataListenerDirectly(DataKey<B> dataKey, DataListener<T> dataListener) {
        List<DataListener<?>> listeners = dataListenerMap.computeIfAbsent(dataKey, k -> new ArrayList<>());
        listeners.add(dataListener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @NotNull List<DataListener<T>> getDataListenersByKey(DataKey<T> dataKey) {
        final Object tmp = dataListenerMap.getOrDefault(dataKey, Collections.emptyList());
        return (List<DataListener<T>>) tmp;
    }

    @Override
    public void addChildModel(Model model) {
        childrenModels.add(model);
    }

    @Override
    public boolean removeChildModel(Model model) {
        return childrenModels.remove(model);
    }

    @Override
    public @Nullable Model getParentModel() {
        return parent;
    }

    @Override
    public @NotNull List<Model> getChildrenModels() {
        return childrenModels;
    }
}
