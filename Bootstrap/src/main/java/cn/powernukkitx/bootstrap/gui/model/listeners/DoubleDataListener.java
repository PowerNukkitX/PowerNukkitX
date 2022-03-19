package cn.powernukkitx.bootstrap.gui.model.listeners;

import cn.powernukkitx.bootstrap.gui.model.DataListener;

public interface DoubleDataListener extends DataListener<Double> {
    @Override
    default Class<Double> getDataClass() {
        return double.class;
    }

    @Override
    void handleUpdate(Double data);
}
