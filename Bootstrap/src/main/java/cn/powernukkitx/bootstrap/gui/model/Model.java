package cn.powernukkitx.bootstrap.gui.model;

import cn.powernukkitx.bootstrap.gui.exception.model.InvalidDataClassException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Model {
    String getName();

    <T> T getDataDirectly(DataKey<T> dataKey);

    default <T> T getData(DataKey<T> dataKey) {
        final T data = getDataDirectly(dataKey);
        if (data != null) return data;
        else {
            final Model parent = getParentModel();
            if (parent != null) {
                return parent.getData(dataKey);
            } else {
                return null;
            }
        }
    }

    <T> void setDataDirectly(DataKey<T> dataKey, T data);

    default <T> void setData(DataKey<T> dataKey, T data) {
        if (!dataKey.clazz.isInstance(data)) {
            throw new InvalidDataClassException(dataKey);
        }
        setDataDirectly(dataKey, data);
        broadcastUpdateData(dataKey, data);
    }

    <T, B extends T> void addDataListenerDirectly(DataKey<B> dataKey, DataListener<T> dataListener);

    @NotNull
    <T> List<DataListener<T>> getDataListenersByKey(DataKey<T> dataKey);

    default <T, B extends T> void listenData(DataKey<B> dataKey, DataListener<T> dataListener) {
        if (!dataKey.getClazz().isAssignableFrom(dataListener.getDataClass())) {
            throw new InvalidDataClassException(dataKey);
        }
        addDataListenerDirectly(dataKey, dataListener);
    }

    default <T> void broadcastUpdateData(DataKey<T> dataKey) {
        broadcastUpdateData(dataKey, getDataDirectly(dataKey));
    }

    default <T> void broadcastUpdateData(DataKey<T> dataKey, T data) {
        for (final DataListener<T> listener : getDataListenersByKey(dataKey)) {
            listener.handleUpdate(data);
        }
        for (final Model child : getChildrenModels()) {
            child.broadcastUpdateData(dataKey, data);
        }
    }

    void addChildModel(Model model);

    boolean removeChildModel(Model model);

    @Nullable
    Model getParentModel();

    @NotNull
    List<Model> getChildrenModels();
}
