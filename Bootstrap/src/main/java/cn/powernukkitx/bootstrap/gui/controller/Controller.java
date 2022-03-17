package cn.powernukkitx.bootstrap.gui.controller;

import cn.powernukkitx.bootstrap.gui.exception.model.InvalidDataClassException;
import cn.powernukkitx.bootstrap.gui.exception.view.InvalidViewClassException;
import cn.powernukkitx.bootstrap.gui.model.DataKey;
import cn.powernukkitx.bootstrap.gui.model.DataListener;
import cn.powernukkitx.bootstrap.gui.model.Model;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.ViewKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface Controller {
    List<Class<? extends View<?>>> getAvailableViews();

    List<? extends View<?>> getAliveViews();

    default <T extends View<?>> List<T> getAliveViews(ViewKey<T> viewKey) {
        final List<? extends View<?>> views = getAliveViews();
        final List<T> out = new ArrayList<>(views.size());
        final Class<T> clazz = viewKey.getClazz();
        for (final View<?> each : views) {
            if (clazz.isInstance(each)) {
                out.add(clazz.cast(each));
            }
        }
        return out;
    }

    @Nullable
    default View<?> getViewByID(int viewID) {
        final List<? extends View<?>> views = getAliveViews();
        for (final View<?> each : views) {
            if (viewID == each.getViewID()) {
                return each;
            }
        }
        return null;
    }

    @Nullable
    default <T extends View<?>> T getViewByID(int viewID, Class<T> viewClazz) {
        final View<?> view = getViewByID(viewID);
        if(view == null) return null;
        if (viewClazz.isInstance(view)) {
            return viewClazz.cast(view);
        }
        throw new InvalidViewClassException(view);
    }

    void addView(View<?> view);

    /**
     * @return 原始Model列表，不应该有复制
     */
    @ApiStatus.Internal
    List<Model> getRawModelList();

    default List<Model> getModels() {
        return getRawModelList();
    }

    default <T extends Model> List<T> getModelsByClass(Class<T> modelClass) {
        final List<Model> models = getRawModelList();
        final List<T> out = new ArrayList<>(models.size() / 2);
        for (final Model each : models) {
            if (modelClass.isInstance(each)) {
                out.add(modelClass.cast(each));
            }
        }
        return out;
    }

    default void addModel(Model... models) {
        addModels(Arrays.asList(models));
    }

    default void addModels(Collection<Model> models) {
        getRawModelList().addAll(models);
    }

    default void removeAllModels() {
        getRawModelList().clear();
    }

    default <T extends Model> boolean removeModels(Class<T> modelClass) {
        return getRawModelList().removeIf(modelClass::isInstance);
    }

    default <T, B extends T> void listenData(DataKey<B> dataKey, DataListener<T> dataListener) {
        for (final Model each : getModels()) {
            each.listenData(dataKey, dataListener);
        }
    }

    default <B> void getAndListenData(DataKey<B> dataKey, DataListener<? super B> dataListener) {
        for (final Model each : getModels()) {
            final B data = each.getData(dataKey);
            if(data != null) dataListener.handleUpdate(data);
            each.listenData(dataKey, dataListener);
        }
    }

    default <T> void batchSetData(DataKey<T> dataKey, T data) {
        if (!dataKey.getClazz().isInstance(data)) {
            throw new InvalidDataClassException(dataKey);
        }
        for(final Model model : getModels()) {
            model.setData(dataKey, data);
        }
    }

    void start();

}
