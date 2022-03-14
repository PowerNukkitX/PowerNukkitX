package cn.powernukkitx.bootstrap.gui.view.keys;

import cn.powernukkitx.bootstrap.gui.view.EnumViewKey;
import cn.powernukkitx.bootstrap.gui.view.MainWindowView;
import cn.powernukkitx.bootstrap.gui.view.ViewKey;

public final class MainWindowViewKey extends ViewKey<MainWindowView> {
    public static final MainWindowViewKey KEY = new MainWindowViewKey();

    MainWindowViewKey() {
        super(EnumViewKey.MainWindow, MainWindowView.class);
    }
}
