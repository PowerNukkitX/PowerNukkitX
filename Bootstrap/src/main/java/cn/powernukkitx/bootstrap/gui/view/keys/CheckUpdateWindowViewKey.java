package cn.powernukkitx.bootstrap.gui.view.keys;

import cn.powernukkitx.bootstrap.gui.view.EnumViewKey;
import cn.powernukkitx.bootstrap.gui.view.ViewKey;
import cn.powernukkitx.bootstrap.gui.view.impl.update.CheckUpdateWindowView;

public final class CheckUpdateWindowViewKey extends ViewKey<CheckUpdateWindowView> {
    public static final CheckUpdateWindowViewKey KEY = new CheckUpdateWindowViewKey();

    CheckUpdateWindowViewKey() {
        super(EnumViewKey.CheckUpdateView, CheckUpdateWindowView.class);
    }
}
