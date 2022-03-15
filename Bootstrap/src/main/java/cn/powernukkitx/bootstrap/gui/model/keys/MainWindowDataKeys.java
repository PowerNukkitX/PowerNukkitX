package cn.powernukkitx.bootstrap.gui.model.keys;

import cn.powernukkitx.bootstrap.gui.model.DataKey;
import cn.powernukkitx.bootstrap.gui.model.EnumDataKey;
import cn.powernukkitx.bootstrap.gui.model.values.TerminalTty;
import com.jediterm.terminal.TtyConnector;

import java.awt.*;

public final class MainWindowDataKeys {
    public static final TitleKey TITLE = new TitleKey();
    public static final WindowSizeKey WINDOW_SIZE = new WindowSizeKey();
    public static final IconKey ICON = new IconKey();
    public static final TerminalTtyKey TERMINAL_TTY = new TerminalTtyKey();
    public static final ServerRunningKey SERVER_RUNNING = new ServerRunningKey();

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

    public static class TerminalTtyKey extends DataKey<TtyConnector> {
        TerminalTtyKey() {
            super(EnumDataKey.TerminalTty, TtyConnector.class);
        }
    }

    public static class ServerRunningKey extends DataKey<Boolean> {
        ServerRunningKey() {
            super(EnumDataKey.ServerRunning, Boolean.class);
        }
    }
}