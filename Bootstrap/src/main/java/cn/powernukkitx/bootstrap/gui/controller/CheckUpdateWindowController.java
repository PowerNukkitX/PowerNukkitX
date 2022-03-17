package cn.powernukkitx.bootstrap.gui.controller;

import cn.powernukkitx.bootstrap.gui.model.impl.view.UpdateWindowModel;
import cn.powernukkitx.bootstrap.gui.model.keys.UpdateWindowDataKeys;
import cn.powernukkitx.bootstrap.gui.model.values.JarLocationsWarp;
import cn.powernukkitx.bootstrap.gui.model.values.JavaLocationsWarp;
import cn.powernukkitx.bootstrap.gui.model.values.LibLocationsWarp;
import cn.powernukkitx.bootstrap.gui.model.values.Warp;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.impl.update.CheckUpdateWindowView;
import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.LibsLocator;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

import static cn.powernukkitx.bootstrap.Bootstrap.workingDir;
import static cn.powernukkitx.bootstrap.util.LanguageUtils.tr;

public final class CheckUpdateWindowController extends CommonController {
    private static final List<Class<? extends View<?>>> VIEW_CLASS = Collections.singletonList(CheckUpdateWindowView.class);

    private final CheckUpdateWindowView checkUpdateWindowView;
    private final UpdateWindowModel updateWindowModel;

    public CheckUpdateWindowController() {
        this.checkUpdateWindowView = new CheckUpdateWindowView(this);
        this.updateWindowModel = new UpdateWindowModel();
        this.views.add(checkUpdateWindowView);
        this.models.add(updateWindowModel);
    }

    @Override
    public List<Class<? extends View<?>>> getAvailableViews() {
        return VIEW_CLASS;
    }

    @Override
    public void start() {
        SwingUtilities.invokeLater(checkUpdateWindowView::init);
    }

    public void onOpen() {
        this.batchSetData(UpdateWindowDataKeys.DISPLAY, true);
        collectData();
    }

    public void onResize(Dimension d) {
        updateWindowModel.setData(UpdateWindowDataKeys.WINDOW_SIZE, d);
    }

    public void onClose() {
        updateWindowModel.setData(UpdateWindowDataKeys.DISPLAY, false);
    }

    public void onRefresh() {
        collectData();
    }

    public void collectData() {
        updateWindowModel.setData(UpdateWindowDataKeys.TITLE, tr("gui.update-window.title.collecting", "0"));
        new SwingWorker<Void, Warp<?>>() {
            @Override
            protected Void doInBackground() {
                publish(new JavaLocationsWarp(new JavaLocator(null, true).locate()));
                publish(new JarLocationsWarp(new JarLocator(workingDir, "cn.nukkit.api.PowerNukkitOnly").locate()));
                publish(new LibLocationsWarp(new LibsLocator().locate()));
                return null;
            }

            @Override
            protected void done() {
                updateWindowModel.setData(UpdateWindowDataKeys.TITLE, tr("gui.update-window.title"));
            }

            @Override
            protected void process(List<Warp<?>> chunks) {
                updateWindowModel.setData(UpdateWindowDataKeys.TITLE, tr("gui.update-window.title.collecting", String.valueOf(chunks.size())));
                for (final Warp<?> tmp : chunks) {
                    if(tmp.isConsumed()) continue;
                    tmp.consume();
                    if(tmp instanceof JavaLocationsWarp) {
                        updateWindowModel.setData(UpdateWindowDataKeys.JAVA_LOCATIONS, (JavaLocationsWarp) tmp);
                    }else if(tmp instanceof JarLocationsWarp) {
                        updateWindowModel.setData(UpdateWindowDataKeys.PNX_LOCATIONS, (JarLocationsWarp) tmp);
                    }else if(tmp instanceof LibLocationsWarp) {
                        updateWindowModel.setData(UpdateWindowDataKeys.LIBS_LOCATIONS, (LibLocationsWarp) tmp);
                    }
                }
            }
        }.execute();
    }
}
