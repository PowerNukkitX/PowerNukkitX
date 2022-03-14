package cn.powernukkitx.bootstrap;

import cn.powernukkitx.bootstrap.gui.controller.MainWindowController;
import com.formdev.flatlaf.FlatDarculaLaf;

public final class GUI implements Program {
    @Override
    public void exec(String... args) {
        // TODO: 2022/3/12 完成GUI
        FlatDarculaLaf.setup();
        // 创建主窗口
        final MainWindowController mainWindowController = new MainWindowController();
        mainWindowController.start();
    }
}
