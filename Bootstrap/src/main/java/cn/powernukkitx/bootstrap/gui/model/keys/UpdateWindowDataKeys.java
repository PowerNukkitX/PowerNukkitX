package cn.powernukkitx.bootstrap.gui.model.keys;

import cn.powernukkitx.bootstrap.gui.model.DataKey;
import cn.powernukkitx.bootstrap.gui.model.EnumDataKey;
import cn.powernukkitx.bootstrap.gui.model.values.ComponentLocationsWarp;
import cn.powernukkitx.bootstrap.gui.model.values.JarLocationsWarp;
import cn.powernukkitx.bootstrap.gui.model.values.JavaLocationsWarp;
import cn.powernukkitx.bootstrap.gui.model.values.LibLocationsWarp;
import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.LibsLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;

import java.awt.*;
import java.util.List;

public final class UpdateWindowDataKeys {
    public static final TitleKey TITLE = new TitleKey();
    public static final WindowSizeKey WINDOW_SIZE = new WindowSizeKey();
    public static final IconKey ICON = new IconKey();
    public static final OnDisplayKey DISPLAY = new OnDisplayKey();
    public static final JavaLocationsKey JAVA_LOCATIONS = new JavaLocationsKey();
    public static final PNXLocationsKey PNX_LOCATIONS = new PNXLocationsKey();
    public static final LibsLocationsKey LIBS_LOCATIONS = new LibsLocationsKey();
    public static final ComponentsLocationsKey COMPONENTS_LOCATIONS = new ComponentsLocationsKey();

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

    public static class JavaLocationsKey extends DataKey<JavaLocationsWarp> {
        JavaLocationsKey() {
            super(EnumDataKey.JavaLocations, JavaLocationsWarp.class);
        }
    }

    public static class PNXLocationsKey extends DataKey<JarLocationsWarp> {
        PNXLocationsKey() {
            super(EnumDataKey.PNXLocations, JarLocationsWarp.class);
        }
    }

    public static class LibsLocationsKey extends DataKey<LibLocationsWarp> {
        LibsLocationsKey() {
            super(EnumDataKey.LibLocations, LibLocationsWarp.class);
        }
    }

    public static class ComponentsLocationsKey extends DataKey<ComponentLocationsWarp> {
        ComponentsLocationsKey() {
            super(EnumDataKey.ComponentLocations, ComponentLocationsWarp.class);
        }
    }
}
