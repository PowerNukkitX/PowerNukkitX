package cn.powernukkitx.bootstrap.gui.view.keys;

import cn.powernukkitx.bootstrap.gui.view.EnumViewKey;
import cn.powernukkitx.bootstrap.gui.view.impl.main.TerminalView;
import cn.powernukkitx.bootstrap.gui.view.ViewKey;

public final class TerminalViewKey extends ViewKey<TerminalView> {
    public static final TerminalViewKey KEY = new TerminalViewKey();

    TerminalViewKey() {
        super(EnumViewKey.TerminalView, TerminalView.class);
    }
}
