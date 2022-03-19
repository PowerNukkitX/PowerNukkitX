package cn.powernukkitx.bootstrap.gui.view.impl.main;

import cn.powernukkitx.bootstrap.gui.controller.Controller;
import cn.powernukkitx.bootstrap.gui.controller.MainWindowController;
import cn.powernukkitx.bootstrap.gui.model.keys.MainWindowDataKeys;
import cn.powernukkitx.bootstrap.gui.view.SwingView;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.ViewKey;
import cn.powernukkitx.bootstrap.gui.view.keys.TerminalViewKey;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import com.jediterm.terminal.ui.settings.SettingsProvider;

public final class TerminalView implements SwingView<JediTermWidget> {
    private final int viewID;
    private final MainWindowController controller;
    private JediTermWidget terminalWidget;
    private SettingsProvider terminalSettings;
    private TtyConnector terminalTty;

    public TerminalView(MainWindowController controller) {
        this.viewID = View.newViewID();
        this.controller = controller;
    }

    @Override
    public ViewKey<TerminalView> getViewKey() {
        return TerminalViewKey.KEY;
    }

    @Override
    public int getViewID() {
        return viewID;
    }

    @Override
    public void init() {
        this.terminalSettings = new DefaultSettingsProvider();
        this.terminalWidget = new JediTermWidget(terminalSettings);
        bind(MainWindowDataKeys.TERMINAL_TTY, TtyConnector.class, value -> {
            if(value != null) {
                if(getTerminalTty() != null) {
                    terminalWidget.stop();
                }
                setTerminalTty(value);
                terminalWidget.setTtyConnector(value);
                terminalWidget.start();
            }
        });
    }

    @Override
    public JediTermWidget getActualComponent() {
        return this.terminalWidget;
    }

    @Override
    public Controller getController() {
        return controller;
    }

    public JediTermWidget getTerminalWidget() {
        return terminalWidget;
    }

    public SettingsProvider getTerminalSettings() {
        return terminalSettings;
    }

    public TtyConnector getTerminalTty() {
        return terminalTty;
    }

    public TerminalView setTerminalTty(TtyConnector terminalTty) {
        this.terminalTty = terminalTty;
        return this;
    }
}
