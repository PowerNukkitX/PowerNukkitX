package cn.powernukkitx.bootstrap.gui.model.impl.view;

import cn.powernukkitx.bootstrap.GUI;
import cn.powernukkitx.bootstrap.gui.model.impl.CommonModel;
import cn.powernukkitx.bootstrap.util.LanguageUtils;

import java.awt.*;

import static cn.powernukkitx.bootstrap.gui.model.keys.UpdateWindowDataKeys.*;

public final class UpdateWindowModel extends CommonModel {
    public UpdateWindowModel() {
        super(CommonModel.INSTANCE);
        init();
    }

    private void init() {
        this.setDataDirectly(TITLE, LanguageUtils.tr("gui.update-window.title"));
        this.setDataDirectly(WINDOW_SIZE, new Dimension(480, 320));
        this.setDataDirectly(ICON, Toolkit.getDefaultToolkit().createImage(GUI.class.getClassLoader().getResource("image/update.png")));
    }
}
