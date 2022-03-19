package cn.powernukkitx.bootstrap.gui.model.listeners;

import cn.powernukkitx.bootstrap.gui.model.DataListener;

public interface BooleanDataListener extends DataListener<Boolean> {
    @Override
    default Class<Boolean> getDataClass() {
        return boolean.class;
    }

    @Override
    void handleUpdate(Boolean data);
}
