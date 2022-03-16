package cn.powernukkitx.bootstrap.gui.view.impl.update;

import cn.powernukkitx.bootstrap.gui.controller.CheckUpdateWindowController;
import cn.powernukkitx.bootstrap.gui.controller.Controller;
import cn.powernukkitx.bootstrap.gui.model.keys.UpdateWindowDataKeys;
import cn.powernukkitx.bootstrap.gui.view.SwingView;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.keys.CheckUpdateWindowViewKey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class CheckUpdateWindowView extends JFrame implements SwingView<JFrame> {
    private final int viewID = View.newViewID();
    private final CheckUpdateWindowController controller;

    public CheckUpdateWindowView(CheckUpdateWindowController controller) {
        this.controller = controller;
    }

    @Override
    public CheckUpdateWindowViewKey getViewKey() {
        return CheckUpdateWindowViewKey.KEY;
    }

    @Override
    public int getViewID() {
        return viewID;
    }

    @Override
    public void init() {
        bind(UpdateWindowDataKeys.ICON, Image.class, this::setIconImage);
        bind(UpdateWindowDataKeys.TITLE, String.class, this::setTitle);
        bind(UpdateWindowDataKeys.WINDOW_SIZE, Dimension.class, this::setSize);
        bind(UpdateWindowDataKeys.DISPLAY, Boolean.class, this::setVisible);

        /* 初始化swing界面 */
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        // 居中显示
        final Point pointScreenCenter = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        this.setLocation(pointScreenCenter.x - 240, pointScreenCenter.y - 180);
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
    public Controller getController() {
        return controller;
    }
}
