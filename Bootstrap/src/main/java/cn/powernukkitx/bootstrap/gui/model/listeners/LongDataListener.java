package cn.powernukkitx.bootstrap.gui.model.listeners;

import cn.powernukkitx.bootstrap.gui.model.DataListener;

public interface LongDataListener extends DataListener<Long> {
    @Override
    default Class<Long> getDataClass() {
        return long.class;
    }

    @Override
    void handleUpdate(Long data);
}
