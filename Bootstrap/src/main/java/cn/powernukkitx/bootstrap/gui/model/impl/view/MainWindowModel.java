package cn.powernukkitx.bootstrap.gui.model.impl.view;

import cn.powernukkitx.bootstrap.GUI;
import cn.powernukkitx.bootstrap.gui.model.Model;
import cn.powernukkitx.bootstrap.gui.model.impl.CommonModel;
import cn.powernukkitx.bootstrap.util.LanguageUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import static cn.powernukkitx.bootstrap.gui.model.keys.MainWindowDataKeys.*;

public final class MainWindowModel extends CommonModel {
    public MainWindowModel() {
        super(CommonModel.INSTANCE);
        init();
    }

    private void init() {
        this.setDataDirectly(TITLE, LanguageUtils.tr("gui.main-window.title"));
        this.setDataDirectly(WINDOW_SIZE, new Dimension(720, 480));
        this.setDataDirectly(ICON, Toolkit.getDefaultToolkit().createImage(GUI.class.getClassLoader().getResource("image/pnx.png")));
        this.setDataDirectly(SERVER_RUNNING, false);
    }

    @Override
    public @NotNull List<Model> getChildrenModels() {
        return Collections.emptyList();
    }
}
