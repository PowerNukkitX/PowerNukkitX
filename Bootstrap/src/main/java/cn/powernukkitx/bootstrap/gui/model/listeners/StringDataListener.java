package cn.powernukkitx.bootstrap.gui.model.listeners;

import cn.powernukkitx.bootstrap.gui.model.DataListener;

public interface StringDataListener extends DataListener<String> {
    @Override
    default Class<String> getDataClass() {
        return String.class;
    }

    @Override
    void handleUpdate(String data);
}
