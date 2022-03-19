package cn.powernukkitx.bootstrap.gui.view.keys;

import cn.powernukkitx.bootstrap.gui.view.EnumViewKey;
import cn.powernukkitx.bootstrap.gui.view.ViewKey;
import cn.powernukkitx.bootstrap.gui.view.impl.monitor.PerformanceView;

public final class PerformanceViewKey extends ViewKey<PerformanceView> {
    public static final PerformanceViewKey KEY = new PerformanceViewKey();

    PerformanceViewKey() {
        super(EnumViewKey.PerformanceView, PerformanceView.class);
    }
}
