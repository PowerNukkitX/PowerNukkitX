package cn.powernukkitx.bootstrap.gui.controller;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.gui.model.impl.CommonModel;
import cn.powernukkitx.bootstrap.gui.model.impl.view.MainWindowModel;
import cn.powernukkitx.bootstrap.gui.model.impl.view.PerformanceWindowModel;
import cn.powernukkitx.bootstrap.gui.model.keys.MainWindowDataKeys;
import cn.powernukkitx.bootstrap.gui.model.values.TerminalTty;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.impl.main.MainWindowView;
import cn.powernukkitx.bootstrap.gui.view.impl.main.TerminalView;
import cn.powernukkitx.bootstrap.info.locator.*;
import cn.powernukkitx.bootstrap.util.ConfigUtils;
import com.jediterm.terminal.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static cn.powernukkitx.bootstrap.Bootstrap.workingDir;
import static cn.powernukkitx.bootstrap.util.LanguageUtils.tr;

public final class MainWindowController extends CommonController {
    public static final List<Class<? extends View<?>>> VIEW_CLASSES =
            Collections.unmodifiableList(Arrays.asList(MainWindowView.class, TerminalView.class));

    private final MainWindowModel mainWindowViewModel;
    private final MainWindowView mainWindowView;
    private final TerminalView terminalView;

    private final CheckUpdateWindowController checkUpdateWindowController;
    private final PerformanceWindowController performanceWindowController;

    private TerminalTty terminalTty = null;
    private Thread pnxThread = null;

    public MainWindowController() {
        addModel(this.mainWindowViewModel = new MainWindowModel());
        views.add(this.mainWindowView = new MainWindowView(this));
        views.add(this.terminalView = new TerminalView(this));
        // 创建其他窗口控制器
        this.checkUpdateWindowController = new CheckUpdateWindowController();
        this.performanceWindowController = new PerformanceWindowController();
    }

    @Override
    public void start() {
        SwingUtilities.invokeLater(() -> {
            this.terminalView.init();
            this.mainWindowView.init();
        });
        this.checkUpdateWindowController.start();
        this.performanceWindowController.start();
    }

    @Override
    public List<Class<? extends View<?>>> getAvailableViews() {
        return VIEW_CLASSES;
    }

    public MainWindowView getMainWindowView() {
        return mainWindowView;
    }

    public MainWindowModel getMainWindowModel() {
        return mainWindowViewModel;
    }

    public TerminalView getTerminalView() {
        return terminalView;
    }

    public TerminalTty getTerminalTty() {
        return terminalTty;
    }

    public void onResize(Dimension d) {
        mainWindowViewModel.setData(MainWindowDataKeys.WINDOW_SIZE, d);
    }

    public Thread getPnxThread() {
        return pnxThread;
    }

    public void onStartServer() {
        mainWindowViewModel.setData(MainWindowDataKeys.TITLE, tr("gui.main-window.title.starting"));
        new SwingWorker<Pair<Location<JavaLocator.JavaInfo>, Location<JarLocator.JarInfo>>, Void>() {
            @Override
            protected Pair<Location<JavaLocator.JavaInfo>, Location<JarLocator.JarInfo>> doInBackground() {
                final List<Location<JavaLocator.JavaInfo>> javaLocations = new JavaLocator("17", true).locate();
                if (javaLocations.size() == 0) {
                    int res = JOptionPane.showConfirmDialog(mainWindowView, tr("gui.std-dialog.error.no-java17"), tr("gui.std-dialog.error.failed-to-start-pnx"),
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (res == 0) {
                        onOpenCheckUpdateWindow();
                    }
                    return null;
                }
                final Location<JavaLocator.JavaInfo> javaInfo = javaLocations.get(0);

                final List<Location<JarLocator.JarInfo>> pnxLocations = new JarLocator(workingDir, "cn.nukkit.api.PowerNukkitOnly").locate();
                if (pnxLocations.size() == 0) {
                    int res = JOptionPane.showConfirmDialog(mainWindowView, tr("gui.std-dialog.error.no-pnx"), tr("gui.std-dialog.error.failed-to-start-pnx"),
                            JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (res == 0) {
                        onOpenCheckUpdateWindow();
                    }
                    return null;
                } else if (pnxLocations.size() > 1) {
                    final StringBuilder sb = new StringBuilder();
                    for (final Location<JarLocator.JarInfo> each : pnxLocations) {
                        sb.append(each.getFile().getName()).append("\n");
                    }
                    JOptionPane.showMessageDialog(mainWindowView, tr("gui.std-dialog.error.no-pnx"), tr("gui.std-dialog.error.multi-conflict-pnx", sb.toString()), JOptionPane.ERROR_MESSAGE);
                    return null;
                } else {
                    return new Pair<>(javaInfo, pnxLocations.get(0));
                }
            }

            @Override
            protected void done() {
                final String fileName;
                try {
                    fileName = get().getSecond().getFile().getName();
                    String cmd = ConfigUtils.startCommand()
                            .replace("%JAVA%", get().getFirst().getFile().getAbsolutePath())
                            .replace("%PNX%", fileName)
                            .replace("%CP_SPLIT%", Locator.platformSplitter());
                    // 添加GraalJIT
                    List<Location<String>> graalJitLocations = new GraalJitLocator().locate();
                    if (graalJitLocations.size() == 2) {
                        cmd = cmd.replace("%GRAAL_JIT%", graalJitLocations.get(0).getFile().getAbsolutePath() + Locator.platformSplitter() + graalJitLocations.get(1).getFile().getAbsolutePath());
                    } else {
                        cmd = cmd.replace("%GRAAL_JIT%", "").replace("-XX:+EnableJVMCI -XX:+UseJVMCICompiler ", "");
                    }
                    cmd = cmd.replace("--upgrade-module-path=" + Locator.platformSplitter() + " ", "");
                    // 添加Graal Module
                    List<Location<Void>> graalModuleLocations = new GraalModuleLocator().locate();
                    if (graalModuleLocations.size() != 0) {
                        StringBuilder sb = new StringBuilder();
                        for (Location<Void> graalModuleLocation : graalModuleLocations) {
                            sb.append(Locator.platformSplitter()).append(graalModuleLocation.getFile().getAbsolutePath());
                        }
                        cmd = cmd.replace("%GRAAL_SDK%", sb.toString().replaceFirst(Locator.platformSplitter(), ""));
                    } else {
                        cmd = cmd.replace("%GRAAL_SDK%", "");
                    }
                    cmd = cmd.replace("--module-path=" + Locator.platformSplitter() + " ", "");
                    // 设置最大内存
                    cmd = cmd.replace("%MEMORY_SIZE_MB%", String.valueOf(PerformanceWindowModel.systemInfo.getHardware().getMemory().getTotal() / 1024 / 1024));
                    final String finalCmd = cmd;
                    pnxThread = new Thread(() -> {
                        try {
                            final Process process = new ProcessBuilder(finalCmd.split(" +")).start();
                            terminalTty = new TerminalTty(process, terminalView.getActualComponent().getTerminal(), terminalView.getTerminalWidget().getTerminalTextBuffer());
                            mainWindowViewModel.setData(MainWindowDataKeys.TERMINAL_TTY, terminalTty);
                            mainWindowViewModel.setData(MainWindowDataKeys.SERVER_RUNNING, true);
                            mainWindowViewModel.setData(MainWindowDataKeys.TITLE, tr("gui.main-window.title.running"));
                            process.waitFor();
                            process.destroy();
                            mainWindowViewModel.setData(MainWindowDataKeys.SERVER_RUNNING, false);
                            mainWindowViewModel.setData(MainWindowDataKeys.TITLE, tr("gui.main-window.title"));
                        } catch (Exception e) {
                            mainWindowViewModel.setData(MainWindowDataKeys.SERVER_RUNNING, false);
                            mainWindowViewModel.setData(MainWindowDataKeys.TITLE, tr("gui.main-window.title"));
                            JOptionPane.showMessageDialog(mainWindowView, tr("failed-to-start-pnx"), tr("gui.std-dialog.error.launch-error", e.getMessage()), JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    pnxThread.start();
                } catch (InterruptedException | ExecutionException e) {
                    mainWindowViewModel.setData(MainWindowDataKeys.SERVER_RUNNING, false);
                    mainWindowViewModel.setData(MainWindowDataKeys.TITLE, tr("gui.main-window.title"));
                    JOptionPane.showMessageDialog(mainWindowView, tr("gui.std-dialog.error.no-pnx"), tr("gui.std-dialog.error.launch-error", e.getMessage()), JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public void onClose() {
        try {
            if (terminalTty != null && terminalTty.isConnected())
                terminalTty.write("\r\nstop");
            CommonModel.TIMER.cancel();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainWindowView, tr("gui.std-dialog.error.no-pnx"), tr("gui.std-dialog.error.stop-error", e.getMessage()), JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.exit(0);
    }

    public void onCloseServer() {
        try {
            if (terminalTty != null && terminalTty.isConnected())
                terminalTty.write("\r\nstop");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainWindowView, tr("gui.std-dialog.error.no-pnx"), tr("gui.std-dialog.error.stop-error", e.getMessage()), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onRestartServer() {
        onCloseServer();
        onStartServer();
    }

    public void onOpenCheckUpdateWindow() {
        checkUpdateWindowController.onOpen();
    }

    public void onOpenPerformanceWindow() {
        performanceWindowController.onOpen();
    }
}
