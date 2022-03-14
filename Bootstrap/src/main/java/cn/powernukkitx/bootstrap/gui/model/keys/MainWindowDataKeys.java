package cn.powernukkitx.bootstrap.gui.model.keys;

import cn.powernukkitx.bootstrap.gui.model.DataKey;
import cn.powernukkitx.bootstrap.gui.model.EnumDataKey;

import java.awt.*;

public final class MainWindowDataKeys {
    public static final TitleKey TITLE = new TitleKey();
    public static final WindowSizeKey WINDOW_SIZE = new WindowSizeKey();
    public static final IconKey ICON = new IconKey();

    public static class TitleKey extends DataKey<String> {
        TitleKey() {
            super(EnumDataKey.MainWindowTitle, String.class);
        }
    }

    public static class WindowSizeKey extends DataKey<Dimension> {
        WindowSizeKey() {
            super(EnumDataKey.MainWindowSize, Dimension.class);
        }
    }

    public static class IconKey extends DataKey<Image> {
        IconKey() {
            super(EnumDataKey.MainWindowIcon, Image.class);
        }
    }
}
