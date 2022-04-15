package cn.powernukkitx.bootstrap.gui.controller;

import cn.powernukkitx.bootstrap.gui.model.impl.view.UpdateWindowModel;
import cn.powernukkitx.bootstrap.gui.model.keys.UpdateWindowDataKeys;
import cn.powernukkitx.bootstrap.gui.model.values.*;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.impl.simple.DownloadDialog;
import cn.powernukkitx.bootstrap.gui.view.impl.update.CheckUpdateWindowView;
import cn.powernukkitx.bootstrap.info.locator.ComponentsLocator;
import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.LibsLocator;
import cn.powernukkitx.bootstrap.info.remote.ComponentsHelper;
import cn.powernukkitx.bootstrap.info.remote.VersionListHelper;
import cn.powernukkitx.bootstrap.util.GzipUtils;
import cn.powernukkitx.bootstrap.util.StringUtils;
import cn.powernukkitx.bootstrap.util.URLUtils;
import net.lingala.zip4j.ZipFile;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
        collectData(true, true, true, true);
    }

    public void collectData(boolean java, boolean pnx, boolean libs, boolean components) {
        final int sum = (java ? 1 : 0) + (pnx ? 1 : 0) + (libs ? 1 : 0) + (components ? 1 : 0);
        updateWindowModel.setData(UpdateWindowDataKeys.TITLE, tr("gui.update-window.title.collecting", "0", String.valueOf(sum)));
        new SwingWorker<Void, Warp<?>>() {
            @Override
            protected Void doInBackground() {
                if (java) publish(new JavaLocationsWarp(new JavaLocator(null, true).locate()));
                if (pnx)
                    publish(new JarLocationsWarp(new JarLocator(workingDir, "cn.nukkit.api.PowerNukkitOnly").locate()));
                if (libs) publish(new LibLocationsWarp(new LibsLocator().locate()));
                if (components) publish(new ComponentLocationsWarp(new ComponentsLocator().locate()));
                return null;
            }

            @Override
            protected void done() {
                updateWindowModel.setData(UpdateWindowDataKeys.TITLE, tr("gui.update-window.title"));
            }

            @Override
            protected void process(List<Warp<?>> chunks) {
                updateWindowModel.setData(UpdateWindowDataKeys.TITLE, tr("gui.update-window.title.collecting", String.valueOf(chunks.size()), String.valueOf(sum)));
                for (final Warp<?> tmp : chunks) {
                    if (tmp.isConsumed()) continue;
                    tmp.consume();
                    if (tmp instanceof JavaLocationsWarp) {
                        updateWindowModel.setData(UpdateWindowDataKeys.JAVA_LOCATIONS, (JavaLocationsWarp) tmp);
                    } else if (tmp instanceof JarLocationsWarp) {
                        updateWindowModel.setData(UpdateWindowDataKeys.PNX_LOCATIONS, (JarLocationsWarp) tmp);
                    } else if (tmp instanceof LibLocationsWarp) {
                        updateWindowModel.setData(UpdateWindowDataKeys.LIBS_LOCATIONS, (LibLocationsWarp) tmp);
                    } else if (tmp instanceof ComponentLocationsWarp) {
                        updateWindowModel.setData(UpdateWindowDataKeys.COMPONENTS_LOCATIONS, (ComponentLocationsWarp) tmp);
                    }
                }
            }
        }.execute();
    }

    public void onDownloadJava(int index) {
        URL downloadURL = null;
        switch (index) {
            case 0:
                downloadURL = URLUtils.adopt17URL();
                break;
            case 1:
                downloadURL = URLUtils.graal17URL();
                break;
        }
        if (downloadURL != null) {
            final String suffix = StringUtils.uriSuffix(downloadURL);
            final File target = new File("tmp." + suffix);
            DownloadDialog.openDownloadDialog("Java17", downloadURL.toString(), target, dialog -> {
                dialog.getLabel().setText(tr("gui.download-dialog.uncompressing"));
                if ("zip".equals(suffix)) {
                    final ZipFile zipFile = new ZipFile(target);
                    try {
                        zipFile.extractAll("./java");
                        zipFile.close();
                        dialog.dispose();
                        //noinspection ResultOfMethodCallIgnored
                        target.delete();
                    } catch (IOException e) {
                        dialog.dispose();
                        JOptionPane.showMessageDialog(dialog, tr("gui.download-dialog.failed-uncompress"), tr("gui.common.err"), JOptionPane.ERROR_MESSAGE);
                    }
                } else if ("tar.gz".equals(suffix)) {
                    try {
                        GzipUtils.uncompressTGzipFile(target, new File("./java"));
                        dialog.dispose();
                        //noinspection ResultOfMethodCallIgnored
                        target.delete();
                    } catch (IOException e) {
                        dialog.dispose();
                        JOptionPane.showMessageDialog(dialog, tr("gui.download-dialog.failed-uncompress"), tr("gui.common.err"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
    }

    public void onClearPNX() {
        CompletableFuture.runAsync(() -> {
            final JarLocator pnxLocator = new JarLocator(workingDir, "cn.nukkit.api.PowerNukkitOnly");
            //noinspection ResultOfMethodCallIgnored
            pnxLocator.locate().forEach(e -> e.getFile().delete());
        });
    }

    public void onDownloadPNX(VersionListHelper.VersionEntry version) {
        final URL downloadLink = Objects.requireNonNull(URLUtils.getAssetsLink("core", version.getBranch() + "-" + version.getCommit()));
        final File target = new File("powernukkitx.jar");
        DownloadDialog.openDownloadDialog("PowerNukkitX", downloadLink.toString(), target, dialog -> {
            dialog.dispose();
            collectData(false, true, false, false);
        });
    }

    public void onDownloadLib(String libName) {
        final URL downloadLink = Objects.requireNonNull(URLUtils.getAssetsLink("libs", libName));
        final File target = new File("libs/" + libName);
        DownloadDialog.openDownloadDialog(libName, downloadLink.toString(), target, dialog -> {
            dialog.dispose();
            collectData(false, false, true, false);
        });
    }

    public void onDownloadLibs(List<String> libNames) {
        if (libNames.size() == 0) {
            JOptionPane.showMessageDialog(checkUpdateWindowView.getActualComponent(), tr("gui.update-window.all-libs-are-latest"),
                    tr("gui.common.sign"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        downloadLib(libNames.listIterator(), libNames.get(0));
    }

    private void downloadLib(Iterator<String> it, String libName) {
        final URL downloadLink = Objects.requireNonNull(URLUtils.getAssetsLink("libs", libName));
        final File target = new File("libs/" + libName);
        DownloadDialog.openDownloadDialog(libName, downloadLink.toString(), target, dialog -> {
            dialog.dispose();
            if (it.hasNext()) {
                downloadLib(it, it.next());
            } else {
                collectData(false, false, true, false);
            }
        });
    }

    public void onInstallComponent(String componentName) {
        final List<ComponentsHelper.ComponentEntry> componentEntries = ComponentsHelper.listRemoteComponents();
        for (ComponentsHelper.ComponentEntry componentEntry : componentEntries) {
            if (componentEntry.getName().equals(componentName)) {
                int res = JOptionPane.showConfirmDialog(this.checkUpdateWindowView, tr("gui.update-window.sure-to-install-component", componentEntry.getDescription() + " " + componentEntry.getVersion()),
                        tr("gui.common.sign"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (res == 0) {
                    for (ComponentsHelper.ComponentFile componentFile : componentEntry.getComponentFiles()) {
                        final File target = new File("./components/" + componentEntry.getName() + "/" + componentFile.getFileName());
                        DownloadDialog.openDownloadDialog(componentEntry.getDescription() + " " + componentEntry.getVersion(),
                                componentFile.getDownloadPath(), target, dialog -> {
                                    dialog.dispose();
                                    collectData(false, false, false, true);
                                });
                    }
                }
                break;
            }
        }
    }
}
