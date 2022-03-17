package cn.powernukkitx.bootstrap.util;

import cn.powernukkitx.bootstrap.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.WeakHashMap;

public final class SwingUtils {
    private static final WeakHashMap<String, Icon> iconCache = new WeakHashMap<>(8);
    private static final Color WARN_COLOR = new Color(187, 62, 53);

    public static Icon getIcon(String name, int width, int height) {
        final String id = name + "," + width + "," + height;
        if (iconCache.containsKey(id)) return iconCache.get(id);
        Icon icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(Objects.requireNonNull(GUI.class.getClassLoader().getResource("image/" + name)))
                .getScaledInstance(width, height, Image.SCALE_SMOOTH));
        iconCache.put(id, icon);
        return icon;
    }

    public static Icon getIcon(String name, int size) {
        return getIcon(name, size, size);
    }

    public static JComponent warp(JComponent... jComponents) {
        final Box hBox = Box.createHorizontalBox();
        for (final JComponent component : jComponents) {
            hBox.add(component);
            hBox.add(Box.createHorizontalStrut(8));
        }
        hBox.add(Box.createHorizontalGlue());
        return hBox;
    }

    public static JComponent warp(int paddingLeft, JComponent... jComponents) {
        final Box hBox = Box.createHorizontalBox();
        hBox.add(Box.createHorizontalStrut(paddingLeft));
        for (final JComponent component : jComponents) {
            hBox.add(component);
            hBox.add(Box.createHorizontalStrut(8));
        }
        hBox.add(Box.createHorizontalGlue());
        return hBox;
    }

    public static JLabel br() {
        final JLabel c = new JLabel("                                                                                                                                                    ");
        c.setEnabled(false);
        c.setPreferredSize(new Dimension(99999, 1));
        return c;
    }

    public static JLabel p(String text) {
        final JLabel label = new JLabel(text);
        label.putClientProperty("FlatLaf.styleClass", "medium");
        return label;
    }

    public static JLabel warn(JLabel label) {
        label.setForeground(WARN_COLOR);
        return label;
    }

    public static JLabel h4(String text) {
        final JLabel label = new JLabel(text);
        label.putClientProperty("FlatLaf.styleClass", "h4");
        return label;
    }
}
