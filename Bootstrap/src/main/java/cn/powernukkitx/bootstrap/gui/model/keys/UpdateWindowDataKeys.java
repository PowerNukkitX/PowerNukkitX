package cn.powernukkitx.bootstrap.gui.model.keys;

import cn.powernukkitx.bootstrap.gui.model.DataKey;
import cn.powernukkitx.bootstrap.gui.model.EnumDataKey;

import java.awt.*;

public final class UpdateWindowDataKeys {
    public static final MainWindowDataKeys.TitleKey TITLE = new MainWindowDataKeys.TitleKey();
    public static final MainWindowDataKeys.WindowSizeKey WINDOW_SIZE = new MainWindowDataKeys.WindowSizeKey();
    public static final MainWindowDataKeys.IconKey ICON = new MainWindowDataKeys.IconKey();

    public static class TitleKey extends DataKey<String> {
        TitleKey() {
            super(EnumDataKey.CheckUpdateWindowTitle, String.class);
        }
    }

    public static class WindowSizeKey extends DataKey<Dimension> {
        WindowSizeKey() {
            super(EnumDataKey.CheckUpdateWindowSize, Dimension.class);
        }
    }

    public static class IconKey extends DataKey<Image> {
        IconKey() {
            super(EnumDataKey.CheckUpdateWindowSize, Image.class);
        }
    }
}
