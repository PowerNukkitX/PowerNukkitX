package cn.powernukkitx.bootstrap.gui.view.impl.main;

import cn.powernukkitx.bootstrap.gui.controller.Controller;
import cn.powernukkitx.bootstrap.gui.controller.MainWindowController;
import cn.powernukkitx.bootstrap.gui.model.keys.MainWindowDataKeys;
import cn.powernukkitx.bootstrap.gui.view.SwingView;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.ViewKey;
import cn.powernukkitx.bootstrap.gui.view.keys.MainWindowViewKey;
import cn.powernukkitx.bootstrap.util.LanguageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static cn.powernukkitx.bootstrap.util.LanguageUtils.tr;

public final class MainWindowView extends JFrame implements SwingView<JFrame> {
    private final int viewID = View.newViewID();
    private final MainWindowController controller;

    public MainWindowView(MainWindowController controller) {
        this.controller = controller;
    }

    public void init() {
        bind(MainWindowDataKeys.ICON, Image.class, this::setIconImage);
        bind(MainWindowDataKeys.TITLE, String.class, this::setTitle);
        bind(MainWindowDataKeys.WINDOW_SIZE, Dimension.class, this::setSize);

        /* 添加子组件 */
        // 终端界面
        final TerminalView terminalView = controller.getTerminalView();
        this.setContentPane(terminalView.getActualComponent());
        // 菜单栏
        final JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        {  // 服务端菜单
            final JMenu serverMenu = new JMenu(tr("gui.menu.server"));
            menuBar.add(serverMenu);
            final JMenuItem startServerOption = new JMenuItem(tr("gui.menu.server.start"));
            serverMenu.add(startServerOption);
            startServerOption.addActionListener(e -> controller.onStartServer());
            bind(MainWindowDataKeys.SERVER_RUNNING, Boolean.class, value -> startServerOption.setEnabled(!value));
            final JMenuItem stopServerOption = new JMenuItem(tr("gui.menu.server.stop"));
            serverMenu.add(stopServerOption);
            stopServerOption.addActionListener(e -> controller.onCloseServer());
            bind(MainWindowDataKeys.SERVER_RUNNING, Boolean.class, stopServerOption::setEnabled);
            final JMenuItem restartServerOption = new JMenuItem(tr("gui.menu.server.restart"));
            serverMenu.add(restartServerOption);
            restartServerOption.addActionListener(e -> controller.onRestartServer());
            bind(MainWindowDataKeys.SERVER_RUNNING, Boolean.class, restartServerOption::setEnabled);
        }
        {  // 窗口菜单
            final JMenu windowMenu = new JMenu(tr("gui.menu.window"));
            menuBar.add(windowMenu);
            final JMenuItem checkUpdateWindowOption = new JMenuItem(tr("gui.menu.window.check-update"));
            windowMenu.add(checkUpdateWindowOption);
            checkUpdateWindowOption.addActionListener(e -> controller.onOpenCheckUpdateWindow());
            final JMenuItem performanceOption = new JMenuItem(tr("gui.menu.window.performance"));
            windowMenu.add(performanceOption);
            performanceOption.addActionListener(e -> controller.onOpenPerformanceWindow());
        }
        /* 初始化swing界面 */
        this.setVisible(true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        // 居中显示
        final Point pointScreenCenter = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        this.setLocation(pointScreenCenter.x - 360, pointScreenCenter.y - 240);
        // 监听窗口变化并反馈给控制器
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { // 监听重置大小
                controller.onResize(e.getComponent().getSize());
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.onClose();
            }
        });
    }

    @Override
    public JFrame getActualComponent() {
        return this;
    }

    @Override
    public ViewKey<MainWindowView> getViewKey() {
        return MainWindowViewKey.KEY;
    }

    @Override
    public int getViewID() {
        return viewID;
    }

    @Override
    public Controller getController() {
        return controller;
    }
}
