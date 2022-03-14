package cn.powernukkitx.bootstrap.gui.model.impl.view;

import cn.powernukkitx.bootstrap.GUI;
import cn.powernukkitx.bootstrap.gui.model.impl.CommonModel;
import cn.powernukkitx.bootstrap.util.LanguageUtils;

import java.awt.*;

import static cn.powernukkitx.bootstrap.gui.model.keys.MainWindowDataKeys.*;

public final class MainWindowViewModel extends CommonModel {
    public MainWindowViewModel() {
        super(CommonModel.INSTANCE);
        init();
    }

    private void init() {
        this.setDataDirectly(TITLE, LanguageUtils.tr("gui.main-window.title"));
        this.setDataDirectly(WINDOW_SIZE, new Dimension(720, 480));
        this.setDataDirectly(ICON, Toolkit.getDefaultToolkit().createImage(GUI.class.getClassLoader().getResource("image/pnx.png")));
    }
}
