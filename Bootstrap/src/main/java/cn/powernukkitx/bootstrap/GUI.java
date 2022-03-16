package cn.powernukkitx.bootstrap;

import cn.powernukkitx.bootstrap.gui.controller.MainWindowController;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;

public final class GUI implements Program {
    @Override
    public void exec(String... args) {
        // TODO: 2022/3/12 完成GUI
        FlatDarculaLaf.setup();
        FlatInspector.install( "ctrl shift alt X" );
        FlatUIDefaultsInspector.install( "ctrl shift alt Y" );
        // 创建主窗口
        final MainWindowController mainWindowController = new MainWindowController();
        mainWindowController.start();
    }
}
