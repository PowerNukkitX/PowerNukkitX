package cn.powernukkitx.bootstrap.gui.model.keys;

import cn.powernukkitx.bootstrap.gui.model.DataKey;
import cn.powernukkitx.bootstrap.gui.model.EnumDataKey;

import java.awt.*;

public final class UpdateWindowDataKeys {
    public static final TitleKey TITLE = new TitleKey();
    public static final WindowSizeKey WINDOW_SIZE = new WindowSizeKey();
    public static final IconKey ICON = new IconKey();
    public static final OnDisplayKey DISPLAY = new OnDisplayKey();

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

    public static class OnDisplayKey extends DataKey<Boolean> {
        OnDisplayKey() {
            super(EnumDataKey.CheckUpdateWindowOnDisplay, Boolean.class);
        }
    }
}
