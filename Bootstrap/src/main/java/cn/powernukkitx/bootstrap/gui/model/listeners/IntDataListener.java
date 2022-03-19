package cn.powernukkitx.bootstrap.gui.model.listeners;

import cn.powernukkitx.bootstrap.gui.model.DataListener;

public interface IntDataListener extends DataListener<Integer> {
    @Override
    default Class<Integer> getDataClass() {
        return int.class;
    }

    @Override
    void handleUpdate(Integer data);
}
