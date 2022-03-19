package cn.powernukkitx.bootstrap.gui.model.listeners;

import cn.powernukkitx.bootstrap.gui.model.DataListener;

public interface FloatDataListener extends DataListener<Float> {
    @Override
    default Class<Float> getDataClass() {
        return float.class;
    }

    @Override
    void handleUpdate(Float data);
}
