package cn.powernukkitx.bootstrap.gui.view.impl.update;

import cn.powernukkitx.bootstrap.gui.controller.CheckUpdateWindowController;
import cn.powernukkitx.bootstrap.gui.controller.Controller;
import cn.powernukkitx.bootstrap.gui.model.keys.UpdateWindowDataKeys;
import cn.powernukkitx.bootstrap.gui.model.values.JarLocationsWarp;
import cn.powernukkitx.bootstrap.gui.model.values.JavaLocationsWarp;
import cn.powernukkitx.bootstrap.gui.model.values.LibLocationsWarp;
import cn.powernukkitx.bootstrap.gui.view.SwingView;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.impl.simple.ListChooseDialog;
import cn.powernukkitx.bootstrap.gui.view.keys.CheckUpdateWindowViewKey;
import cn.powernukkitx.bootstrap.info.locator.JarLocator;
import cn.powernukkitx.bootstrap.info.locator.JavaLocator;
import cn.powernukkitx.bootstrap.info.locator.LibsLocator;
import cn.powernukkitx.bootstrap.info.locator.Location;
import cn.powernukkitx.bootstrap.info.remote.VersionListHelper;
import cn.powernukkitx.bootstrap.util.GitUtils;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static cn.powernukkitx.bootstrap.gui.view.impl.update.TreeEntry.*;
import static cn.powernukkitx.bootstrap.util.LanguageUtils.tr;
import static cn.powernukkitx.bootstrap.util.SwingUtils.getIcon;

public final class CheckUpdateWindowView extends JFrame implements SwingView<JFrame> {
    private final int viewID = View.newViewID();
    private final CheckUpdateWindowController controller;
    private final JFrame self = this;
    private final List<LibsLocator.LibInfo> libsToUpdate = new ArrayList<>();

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

        /* 添加组件 */
        final JScrollPane scrollPane = new JScrollPane();
        this.setContentPane(scrollPane);
        bind(UpdateWindowDataKeys.WINDOW_SIZE, Dimension.class, scrollPane::setPreferredSize);
        final JTree tree = new JTree();
        scrollPane.setViewportView(tree);
        tree.setCellRenderer(new CheckUpdateTreeCellRenderer());
        // 菜单
        final JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        {  // 选项菜单
            final JMenu menu = new JMenu(tr("gui.menu.update-option"));
            menuBar.add(menu);
            final JMenuItem refreshOption = new JMenuItem(tr("gui.menu.update-option.refresh"));
            menu.add(refreshOption);
            refreshOption.addActionListener(e -> controller.onRefresh());
            final JMenuItem allLibsOption = new JMenuItem(tr("gui.menu.update-option.update-all-libs"));
            menu.add(allLibsOption);
            allLibsOption.addActionListener(e -> controller.onDownloadLibs(libsToUpdate.stream().map(LibsLocator.LibInfo::getName).collect(Collectors.toList())));
        }
        // 根节点
        final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        tree.setModel(treeModel);
        {  // JVM信息
            final TreeEntry javaEntry = createWaitEntry(tr("gui.update-window.java-runtime"));
            final DefaultMutableTreeNode javaNode = new DefaultMutableTreeNode(javaEntry);
            rootNode.add(javaNode);
            bind(UpdateWindowDataKeys.JAVA_LOCATIONS, JavaLocationsWarp.class, value -> {
                final List<Location<JavaLocator.JavaInfo>> javaLocations = value.get();
                if (javaLocations.size() == 0) {
                    javaEntry.setIcon(getIcon("error.png", TreeEntry.SIZE));
                    safeClearChildren(treeModel, javaNode);
                    final TreeEntry tmpEntry = createErrorEntry(tr("gui.update-window.java-not-found")).setExtra("downloadJVM");
                    final DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpEntry);
                    safeAddChild(treeModel, javaNode, tmpNode);
                } else {
                    List<Location<JavaLocator.JavaInfo>> get = value.get();
                    javaEntry.setIcon(getIcon("ok.png", TreeEntry.SIZE));
                    safeClearChildren(treeModel, javaNode);
                    for (int i = 0, getSize = get.size(); i < getSize; i++) {
                        final Location<JavaLocator.JavaInfo> javaInfoLocation = get.get(i);
                        final JavaLocator.JavaInfo info = javaInfoLocation.getInfo();
                        if (i == 0) {
                            final TreeEntry tmpEntry = "17".equals(info.getMajorVersion()) ?
                                    createOkEntry(info.getFullVersion() + " - " + info.getVendor()) :
                                    createWarnEntry(info.getFullVersion() + " - " + info.getVendor()).setExtra("needNewJVM");
                            final DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpEntry);
                            safeAddChild(treeModel, javaNode, tmpNode);
                            if (!"17".equals(info.getMajorVersion())) {
                                javaEntry.setIcon(getIcon("error.png", TreeEntry.SIZE));
                            }
                        } else {
                            final TreeEntry tmpEntry = "17".equals(info.getMajorVersion()) ?
                                    createComponentEntry(info.getFullVersion() + " - " + info.getVendor()) :
                                    createWarnEntry(info.getFullVersion() + " - " + info.getVendor()).setExtra("needNewJVM");
                            final DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpEntry);
                            safeAddChild(treeModel, javaNode, tmpNode);
                        }
                    }
                }
                tree.repaint();
            });
        }
        {  // PNX信息
            final TreeEntry pnxEntry = createWaitEntry(tr("gui.update-window.pnx"));
            final DefaultMutableTreeNode pnxNode = new DefaultMutableTreeNode(pnxEntry);
            rootNode.add(pnxNode);
            bind(UpdateWindowDataKeys.PNX_LOCATIONS, JarLocationsWarp.class, value -> {
                final List<Location<JarLocator.JarInfo>> pnxLocations = value.get();
                if (pnxLocations.size() == 0) {
                    pnxEntry.setIcon(getIcon("error.png", TreeEntry.SIZE));
                    safeClearChildren(treeModel, pnxNode);
                    final TreeEntry tmpEntry = createErrorEntry(tr("gui.update-window.pnx-not-found")).setExtra("downloadPNX");
                    final DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpEntry);
                    safeAddChild(treeModel, pnxNode, tmpNode);
                } else {
                    pnxEntry.setIcon(getIcon("ok.png", TreeEntry.SIZE));
                    safeClearChildren(treeModel, pnxNode);
                    boolean conflict = false;
                    if (pnxLocations.size() != 1) {
                        conflict = true;
                        pnxEntry.setIcon(getIcon("error.png", TreeEntry.SIZE));
                        final TreeEntry tmpEntry = createErrorEntry(tr("gui.update-window.pnx-multi-conflict")).setExtra("conflictPNX");
                        final DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpEntry);
                        safeAddChild(treeModel, pnxNode, tmpNode);
                    }
                    for (final Location<JarLocator.JarInfo> pnxLocation : pnxLocations) {
                        final JarLocator.JarInfo info = pnxLocation.getInfo();
                        if (info.getGitInfo().isPresent()) {
                            final GitUtils.FullGitInfo gitInfo = info.getGitInfo().get();
                            final TreeEntry tmpEntry = conflict ?
                                    createWarnEntry(gitInfo.getMainVersion() + "-git-" + gitInfo.getCommitID()).setExtra("conflictPNX") :
                                    createOkEntry(gitInfo.getMainVersion() + "-git-" + gitInfo.getCommitID()).setExtra("updatePNX");
                            final DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(tmpEntry);
                            safeAddChild(treeModel, pnxNode, tmpNode);
                        }
                    }
                }
                tree.repaint();
            });
        }
        {  // 依赖库信息
            final TreeEntry libsEntry = createWaitEntry(tr("gui.update-window.libs"));
            final DefaultMutableTreeNode libsNode = new DefaultMutableTreeNode(libsEntry);
            rootNode.add(libsNode);
            bind(UpdateWindowDataKeys.LIBS_LOCATIONS, LibLocationsWarp.class, value -> {
                final List<Location<LibsLocator.LibInfo>> libLocations = value.get();
                libsToUpdate.clear();
                boolean libFull = true;
                safeClearChildren(treeModel, libsNode);
                for (final Location<LibsLocator.LibInfo> location : libLocations) {
                    final LibsLocator.LibInfo info = location.getInfo();
                    DefaultMutableTreeNode tmpNode;
                    if (!info.isExists()) {
                        final TreeEntry tmpEntry = createErrorEntry(info.getName()).setExtra("downloadLib");
                        tmpNode = new DefaultMutableTreeNode(tmpEntry);
                        libFull = false;
                        libsToUpdate.add(info);
                    } else if (info.isNeedsUpdate()) {
                        final TreeEntry tmpEntry = createWarnEntry(info.getName()).setExtra("downloadLib");
                        tmpNode = new DefaultMutableTreeNode(tmpEntry);
                        libFull = false;
                        libsToUpdate.add(info);
                    } else {
                        final TreeEntry treeEntry = createOkEntry(info.getName());
                        tmpNode = new DefaultMutableTreeNode(treeEntry);
                    }
                    safeAddChild(treeModel, libsNode, tmpNode);
                }
                if (!libFull) {
                    libsEntry.setIcon(getIcon("error.png", TreeEntry.SIZE));
                } else {
                    libsEntry.setIcon(getIcon("ok.png", TreeEntry.SIZE));
                }
                tree.repaint();
            });
        }
        {  // 根节点隐藏
            tree.setRootVisible(true);
            tree.expandRow(0);
            tree.setRootVisible(false);
        }
        {
            // 监听点击
            tree.addTreeSelectionListener(e -> SwingUtilities.invokeLater(() -> {
                final Object obj = e.getNewLeadSelectionPath().getLastPathComponent();
                if (obj instanceof DefaultMutableTreeNode) {
                    final Object value = ((DefaultMutableTreeNode) obj).getUserObject();
                    if (value instanceof TreeEntry) {
                        final TreeEntry data = (TreeEntry) value;
                        final String extra = data.getExtra();
                        if (extra != null)
                            switch (extra) {
                                case "needNewJVM":
                                    int res = JOptionPane.showConfirmDialog(this, tr("gui.update-window.need-java17", data.getName()),
                                            tr("gui.common.sign"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                    if (res != 0) break;
                                case "downloadJVM":
                                    ListChooseDialog.openListChooseDialog(tr("gui.update-window.download-java"),
                                            Arrays.asList(tr("gui.update-window.openjdk"), tr("gui.update-window.graalvm")),
                                            controller::onDownloadJava);
                                    break;
                                case "updatePNX":
                                    int res4 = JOptionPane.showConfirmDialog(this, tr("gui.update-window.update-pnx"),
                                            tr("gui.common.sign"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                    if (res4 != 0) break;
                                case "conflictPNX":
                                    if ("conflictPNX".equals(extra)) {
                                        int res2 = JOptionPane.showConfirmDialog(this, tr("gui.update-window.fix-pnx"),
                                                tr("gui.common.sign"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                        if (res2 != 0) break;
                                        controller.onClearPNX();
                                    }
                                case "downloadPNX":
                                    new SwingWorker<List<VersionListHelper.VersionEntry>, Void>() {
                                        @Override
                                        protected List<VersionListHelper.VersionEntry> doInBackground() {
                                            return VersionListHelper.listRemoteVersions("core");
                                        }

                                        @Override
                                        protected void done() {
                                            try {
                                                final List<VersionListHelper.VersionEntry> versions = get();
                                                ListChooseDialog.openListChooseDialog(tr("gui.update-window.download-pnx"),
                                                        versions.stream().map(each -> "git-" + each.getBranch() + "-" + each.getCommit() + " (" + each.getTime() + ")").collect(Collectors.toList()),
                                                        i -> controller.onDownloadPNX(versions.get(i)));
                                            } catch (InterruptedException | ExecutionException ex) {
                                                JOptionPane.showMessageDialog(self, tr("gui.update-window.fail-fetch-pnx-versions"),
                                                        tr("gui.common.err"), JOptionPane.ERROR_MESSAGE);
                                                ex.printStackTrace();
                                            }
                                        }
                                    }.execute();
                                    break;
                                case "downloadLib":
                                    if (libsToUpdate.size() > 0) {
                                        int res3 = JOptionPane.showConfirmDialog(this, tr("gui.update-window.download-all-libs"),
                                                tr("gui.common.sign"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                        if (res3 == 0)
                                            controller.onDownloadLibs(libsToUpdate.stream().map(LibsLocator.LibInfo::getName).collect(Collectors.toList()));
                                        else
                                            controller.onDownloadLib(data.getName());
                                    } else {
                                        controller.onDownloadLib(data.getName());
                                    }
                                    break;
                            }
                    }
                }
            }));
        }
        /* 初始化swing界面 */
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        // 居中显示
        final Point pointScreenCenter = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        this.setLocation(pointScreenCenter.x - 240, pointScreenCenter.y - 160);
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

    private void safeClearChildren(DefaultTreeModel treeModel, DefaultMutableTreeNode treeNode) {
        for (int i = treeNode.getChildCount() - 1; i >= 0; i--) {
            treeModel.removeNodeFromParent((MutableTreeNode) treeNode.getChildAt(i));
        }
    }

    private void safeAddChild(DefaultTreeModel treeModel, DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        treeModel.insertNodeInto(child, parent, parent.getChildCount());
    }
}
