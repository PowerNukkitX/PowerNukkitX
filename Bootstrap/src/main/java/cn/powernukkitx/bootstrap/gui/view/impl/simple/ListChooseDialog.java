package cn.powernukkitx.bootstrap.gui.view.impl.simple;

import javax.swing.*;
import java.util.List;
import java.util.function.IntConsumer;

import static cn.powernukkitx.bootstrap.util.LanguageUtils.tr;

public final class ListChooseDialog extends JDialog {
    private final String title;
    private final List<String> options;
    private final IntConsumer callback;

    private int selected = -1;

    ListChooseDialog(String title, List<String> options, IntConsumer callback) {
        this.title = title;
        this.options = options;
        this.callback = callback;
    }

    private void init() {
        final int height = options.size() * 22;
        {  // 设置标题
            this.setTitle(title);
            this.setVisible(true);
            this.setSize(600, height + 96);
            this.setResizable(false);
        }
        final JPanel panel = new JPanel(null);
        this.setContentPane(panel);
        final JList<String> jList = new JList<>(options.toArray(new String[0]));
        {  // 添加列表
            panel.add(jList);
            jList.setSize(600, height);
            jList.addListSelectionListener(e -> selected = e.getFirstIndex());
        }
        {
            final JButton jButton = new JButton(tr("gui.common.ok"));
            panel.add(jButton);
            jButton.addActionListener(e -> {
                callback.accept(selected);
                this.dispose();
            });
            jList.addListSelectionListener(e -> jButton.setEnabled(true));
            jButton.setEnabled(false);
            jButton.setSize(96, 36);
            jButton.setLocation(474, height + 6);
        }
    }

    public static void openListChooseDialog(String title, List<String> options, IntConsumer callback) {
        final ListChooseDialog dialog = new ListChooseDialog(title, options, callback);
        dialog.init();
        dialog.setLocationRelativeTo(null);
    }
}
