package cn.powernukkitx.bootstrap.gui.view;

import cn.powernukkitx.bootstrap.gui.controller.Controller;
import cn.powernukkitx.bootstrap.gui.controller.MainWindowController;
import cn.powernukkitx.bootstrap.gui.model.keys.MainWindowDataKeys;
import cn.powernukkitx.bootstrap.gui.view.keys.MainWindowViewKey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public final class MainWindowView extends JFrame implements View {
    private final int viewID = View.newViewID();
    private final MainWindowController controller;

    public MainWindowView(MainWindowController controller) {
        this.controller = controller;
    }

    public void init() {
        bind(MainWindowDataKeys.ICON, Image.class, this::setIconImage);
        bind(MainWindowDataKeys.TITLE, String.class, this::setTitle);
        bind(MainWindowDataKeys.WINDOW_SIZE, Dimension.class, this::setSize);

        /* 初始化swing界面 */
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null); //居中
        // 监听窗口变化并反馈给控制器
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { // 监听重置大小
                controller.onResize(e.getComponent().getSize());
            }
        });
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
