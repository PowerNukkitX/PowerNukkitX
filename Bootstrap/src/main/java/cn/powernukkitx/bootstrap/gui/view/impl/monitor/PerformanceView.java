package cn.powernukkitx.bootstrap.gui.view.impl.monitor;

import cn.powernukkitx.bootstrap.gui.controller.Controller;
import cn.powernukkitx.bootstrap.gui.controller.PerformanceWindowController;
import cn.powernukkitx.bootstrap.gui.model.impl.view.PerformanceWindowModel;
import cn.powernukkitx.bootstrap.gui.model.keys.PerformanceDataKeys;
import cn.powernukkitx.bootstrap.gui.view.SwingView;
import cn.powernukkitx.bootstrap.gui.view.View;
import cn.powernukkitx.bootstrap.gui.view.ViewKey;
import cn.powernukkitx.bootstrap.gui.view.keys.PerformanceViewKey;
import cn.powernukkitx.bootstrap.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static cn.powernukkitx.bootstrap.gui.model.keys.PerformanceDataKeys.*;
import static cn.powernukkitx.bootstrap.util.LanguageUtils.tr;
import static cn.powernukkitx.bootstrap.util.SwingUtils.br;

public final class PerformanceView extends JFrame implements SwingView<PerformanceView> {
    private final int viewID = View.newViewID();

    private final PerformanceWindowController controller;

    public PerformanceView(PerformanceWindowController controller) {
        this.controller = controller;
    }

    @Override
    public ViewKey<PerformanceView> getViewKey() {
        return PerformanceViewKey.KEY;
    }

    @Override
    public int getViewID() {
        return viewID;
    }

    @Override
    public void init() {
        bind(PerformanceDataKeys.ICON, Image.class, this::setIconImage);
        bind(PerformanceDataKeys.TITLE, String.class, this::setTitle);
        bind(PerformanceDataKeys.WINDOW_SIZE, Dimension.class, this::setSize);
        bind(PerformanceDataKeys.DISPLAY, Boolean.class, b -> {
            System.out.println(b);
            setVisible(b);
        });

        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setContentPane(panel);
        final PerformanceWindowModel model = controller.getModel();
        {
            String vm = model.getData(IS_IN_VM);
            if (vm != null) {
                panel.add(new JLabel(tr("gui.performance.vm", vm)));
                panel.add(br());
                panel.add(br());
            }
        }
        {
            final JLabel cpuModelVendorLabel = new JLabel();
            panel.add(cpuModelVendorLabel);
            panel.add(br());
            int cpuCoreCount = model.getData(CPU_CORE_COUNT);
            int cpuLogicCount = model.getData(CPU_LOGIC_COUNT);
            String cpuVendor = model.getData(CPU_VENDOR);
            cpuModelVendorLabel.setText(tr("gui.performance.cpu-vendor",  cpuVendor, String.valueOf(cpuCoreCount), String.valueOf(cpuLogicCount)));
        }
        {
            panel.add(br());
            panel.add(new JLabel(tr("gui.performance.status")));
            panel.add(br());
        }
        {
            final JLabel cpuStatusLabel = new JLabel();
            panel.add(cpuStatusLabel);
            panel.add(br());
            final String maxFreq = model.getData(CPU_MHZ);
            bind(CPU_CURRENT_MHZ, String.class, value -> {
                float usage = model.getData(TOTAL_CPU_USAGE);
                String currentFreq = model.getData(CPU_CURRENT_MHZ);
                cpuStatusLabel.setText(tr("gui.performance.cpu-status", String.format("%.2f", usage * 100), currentFreq, maxFreq));
            });
        }
        {
            final JLabel memoryStatusLabel = new JLabel();
            panel.add(memoryStatusLabel);
            final long maxMemory = model.getData(TOTAL_MEMORY);
            bind(TOTAL_MEMORY_USING, Long.class, value -> memoryStatusLabel.setText(tr("gui.performance.memory-status", String.format("%.2f", value * 100.0 / maxMemory),
                    StringUtils.displayableBytes(value), StringUtils.displayableBytes(maxMemory))));
        }

        /* 初始化swing界面 */
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        // 居中显示
        final Point pointScreenCenter = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        this.setLocation(pointScreenCenter.x - 240, pointScreenCenter.y - 160);
        // 处理关闭
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.onClose();
            }
        });
    }

    @Override
    public PerformanceView getActualComponent() {
        return this;
    }

    @Override
    public Controller getController() {
        return controller;
    }
}
